package kiss.export.demo

import io.minio.MinioClient
import io.minio.PutObjectArgs
import kiss.export.ExportTask
import kiss.export.ExportTaskScene
import kiss.export.ExportTaskStatus
import kiss.util.go
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.streaming.DeferredSXSSFWorkbook
import org.apache.poi.xssf.streaming.RowGeneratorFunction
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.jdbc.core.ConnectionCallback
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.PipedInputStream
import java.io.PipedOutputStream

@RestController
@RequestMapping("/export/demo/big-data")
class BigDataService(
    val sql: KSqlClient,
    val jdbcTemplate: JdbcTemplate,
    val minioClient: MinioClient,
) {

    @PostMapping("/generate")
    fun generate(@RequestParam count: Int) {
        val freeMemory = Runtime.getRuntime().freeMemory()
        val chunkSize = 1000
        val chunkCount = (freeMemory / chunkSize).toInt()

        val value = Long.MAX_VALUE.toString()
        val dataSequence = generateSequence(1L) { it + 1 }
            .take(count)
            .map { arrayOf(value, value, value, value, value, value, value, value, value, value) }

        // 分批插入，防止 OOM
        dataSequence
            .chunked(chunkCount)
            .forEach { chunk ->
                jdbcTemplate.batchUpdate(
                    "INSERT INTO big_data (a, b, c, d, e, f, g, h, i, j) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    chunk,
                )
            }
    }

    @PostMapping("/export-task")
    fun createExportTask(): Int {
        val savedTask = sql.transaction {
            sql.save(ExportTask {
                this.scene = ExportTaskScene.BIG_DATA
            }, SaveMode.INSERT_ONLY).modifiedEntity
        }
        val taskId = savedTask.id

        go {
            val status = try {
                generateExcelAndUploadToS3(taskId)
                ExportTaskStatus.DONE
            } catch (ex: Exception) {
                ExportTaskStatus.FAILED
                throw ex
            }
            sql.transaction {
                sql.save(ExportTask {
                    this.id = taskId
                    this.status = status
                }, SaveMode.UPDATE_ONLY)
            }
        }

        return taskId
    }

    private fun generateExcelAndUploadToS3(taskId: Int) {
        val total = jdbcTemplate.queryForObject("SELECT count(*) FROM big_data", Long::class.java)!!
        DeferredSXSSFWorkbook().use { wb ->

            // 每个 sheet 100万行
            val rowNumsOfEachSheet = 100_0000L
            for (start in 0L until total step rowNumsOfEachSheet) {
                val end = minOf(start + rowNumsOfEachSheet, total)
                val sheet = wb.createSheet()
                sheet.setRowGenerator(rowGenerator(start, end))
            }

            val outputStream = PipedOutputStream()
            val inputStream = PipedInputStream(outputStream, 5242880)

            go {
                outputStream.use(wb::write)
            }

            inputStream.use {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket("export-task")
                        .`object`("$taskId")
                        .stream(it, -1, 5242880)
                        .build()
                )
            }
        }
    }

    private fun rowGenerator(start: Long, end: Long) = RowGeneratorFunction { sheet ->
        val freeMemory = Runtime.getRuntime().freeMemory()
        val chunkSize = 1000
        val chunkCount = (freeMemory / chunkSize).toInt()

        jdbcTemplate.execute(ConnectionCallback { conn ->
            conn.autoCommit = false
            val st = conn.createStatement()
            st.fetchSize = chunkCount
            st.executeQuery("SELECT * FROM big_data OFFSET $start LIMIT ${end - start}").use { rs ->
                var rowIndex = 0
                while (rs.next()) {
                    val row = sheet.createRow(rowIndex)
                    row.createCell(0, CellType.STRING).setCellValue(rs.getString("a"))
                    row.createCell(1, CellType.STRING).setCellValue(rs.getString("b"))
                    row.createCell(2, CellType.STRING).setCellValue(rs.getString("c"))
                    row.createCell(3, CellType.STRING).setCellValue(rs.getString("d"))
                    row.createCell(4, CellType.STRING).setCellValue(rs.getString("e"))
                    row.createCell(5, CellType.STRING).setCellValue(rs.getString("f"))
                    row.createCell(6, CellType.STRING).setCellValue(rs.getString("g"))
                    row.createCell(7, CellType.STRING).setCellValue(rs.getString("h"))
                    row.createCell(8, CellType.STRING).setCellValue(rs.getString("i"))
                    row.createCell(9, CellType.STRING).setCellValue(rs.getString("j"))
                    rowIndex++
                }
            }
        })
    }
}
