package kiss.export.demo

import io.minio.http.Method
import kiss.export.ExportTask
import kiss.export.ExportTaskScene
import kiss.export.ExportTaskStatus
import kiss.s3.S3Service
import kiss.util.go
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.streaming.DeferredSXSSFWorkbook
import org.apache.poi.xssf.streaming.RowGeneratorFunction
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.HttpURLConnection
import java.net.URI

@RestController
@RequestMapping("/export/demo/big-data")
class BigDataService(
    val sql: KSqlClient,
    val jdbcTemplate: JdbcTemplate,
    val s3Service: S3Service,
) {

    @PostMapping("/generate")
    fun generate(@RequestParam count: Int) {
        val value = Long.MAX_VALUE.toString()
        val dataSequence = generateSequence(1L) { it + 1 }
            .take(count)
            .map { arrayOf(value, value, value, value, value, value, value, value, value, value) }

        // 分批插入，防止 OOM
        dataSequence
            .chunked(10_0000)
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
        DeferredSXSSFWorkbook().use { wb ->
            val sheet = wb.createSheet()
            sheet.setRowGenerator(rowGenerator)
            val url = s3Service.preSignedUrl(
                bucket = "export-task",
                method = Method.PUT,
                objectName = "$taskId"
            )

            val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.setRequestMethod("PUT")

            connection.outputStream.use(wb::write)
            val responseCode = connection.responseCode
            val responseMessage = connection.responseMessage
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw AsyncExportException("Failed to upload file to S3 server: $responseCode $responseMessage")
            }
        }
    }

    private val rowGenerator = RowGeneratorFunction { sheet ->
        var rowIndex = 0
        sql.createQuery(BigData::class) {
            select(table)
        }.forEach {
            val row = sheet.createRow(rowIndex)
            row.createCell(0, CellType.STRING).setCellValue(it.a)
            row.createCell(1, CellType.STRING).setCellValue(it.b)
            row.createCell(2, CellType.STRING).setCellValue(it.c)
            row.createCell(3, CellType.STRING).setCellValue(it.d)
            row.createCell(4, CellType.STRING).setCellValue(it.e)
            row.createCell(5, CellType.STRING).setCellValue(it.f)
            row.createCell(6, CellType.STRING).setCellValue(it.g)
            row.createCell(7, CellType.STRING).setCellValue(it.h)
            row.createCell(8, CellType.STRING).setCellValue(it.i)
            row.createCell(9, CellType.STRING).setCellValue(it.j)
            rowIndex++
        }
    }
}

class AsyncExportException(message: String) : Exception(message)
