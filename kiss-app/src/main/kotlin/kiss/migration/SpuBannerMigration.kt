package kiss.migration

import kiss.infrastructure.s3.S3Service
import org.springframework.stereotype.Component
import java.net.URI

@Component
class SpuBannerMigration(val s3Service: S3Service) : Migration {
    override val version = 3

    override fun migrate() {
        val banners = listOf(
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/321056/38/16334/110552/68779672Fe7f26431/f5a294b7dcf676dd.png",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/267705/1/12707/18510/6788b9a1F81485818/10849a144f1ab085.jpg",
            "https://img13.360buyimg.com/n2/s480x480_jfs/t1/317366/37/13062/50175/6864a82fF3ab71358/f5ca3a758e24d1cb.png",
            "https://img12.360buyimg.com/n2/s480x480_jfs/t1/290682/25/15803/94309/68530083Fa2db2c56/85175969be854141.jpg",
            "https://img14.360buyimg.com/n2/s480x480_jfs/t1/321431/31/13957/76066/686a3a8aF4e8a2912/f74b6e883ed30dbb.jpg",
            "https://img12.360buyimg.com/n2/s480x480_jfs/t1/247124/38/26515/45305/674eca58F9c3ed33c/715a8d787ab782cf.jpg",
            "https://img13.360buyimg.com/n2/s480x480_jfs/t1/312760/14/21569/69390/688c7f35Fb3f893ad/e4c547dca93011bc.jpg",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/314911/5/12187/58452/685a50a2Fe5af8276/f4a12963894c7cab.jpg",
            "https://img12.360buyimg.com/n2/s480x480_jfs/t1/291415/36/15434/366142/687763c1F6216b263/99e93e2c7376a154.png",
            "https://img14.360buyimg.com/n2/s480x480_jfs/t1/297156/31/24657/48404/6882f2afFea5d845a/25780d809218a435.jpg",
            "https://img14.360buyimg.com/n2/s480x480_jfs/t1/229443/36/35965/63576/6848f4faFc4f02734/7788509c5ecec7e2.png",
            "https://img14.360buyimg.com/n2/s480x480_jfs/t1/313808/4/22060/142515/688c9b87F3f3f831a/93c797ef7e41470b.jpg",
            "https://img13.360buyimg.com/n2/s480x480_jfs/t1/316549/16/10943/478514/6853c6b3F0d80124c/e2991dafb583446f.png",
            "https://img12.360buyimg.com/n2/s480x480_jfs/t1/321076/40/10416/73816/6853c9e9Fa5a85fa2/1ce4be72895903c0.jpg",
            "https://img14.360buyimg.com/n2/s480x480_jfs/t1/315753/10/17245/38871/6878d2c5F7a3c08b6/3394980e25b78289.jpg",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/318116/8/10063/71696/6852e94aF538390cb/b6c101e324df67a4.jpg",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/306093/8/23162/69667/688c7e79Fa28cb938/9ccef46d87458f4e.jpg",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/297501/8/19533/50619/6864a855Fd9248d83/1d427962bab87c03.png",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/321343/27/16756/126818/68772437F72d7cae9/36533d4cc1154d92.jpg",
            "https://img14.360buyimg.com/n2/s480x480_jfs/t1/306767/16/17720/315647/68776370F248e1c6e/9724fe854ce7b0ac.png",
            "https://img12.360buyimg.com/n2/s480x480_jfs/t1/296761/3/27150/100528/688890d2Ff79f15c2/83dcbde2e38778b3.jpg",
            "https://img14.360buyimg.com/n2/s480x480_jfs/t1/314972/4/22004/118075/688cfa53Fd50b6a4b/48ee93063cb4b367.jpg",
            "https://img12.360buyimg.com/n2/s480x480_jfs/t1/271241/31/10308/68132/67e29cb1F1112bb34/8835cdd8c7ce0671.jpg",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/205482/36/48329/69889/673c6c85F3c868f99/ebdb0da376607cae.jpg",
            "https://img13.360buyimg.com/n2/s480x480_jfs/t1/283172/14/17449/36096/67f7de8eF34cd0d88/3abc71e829173230.jpg",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/274877/3/25133/72508/68114d02Fede8f3b1/661d71b57fc64d7a.jpg",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/135157/7/48004/43142/673492eaF8df6e003/50ed5d535ddb29ad.jpg",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/318955/23/20857/79021/688c7e9aFef45dacc/72e01f1f4027402b.jpg",
            "https://img12.360buyimg.com/n2/s480x480_jfs/t1/296198/4/27257/78849/688c1dc1F970fb9bd/144b8f4ebff52e0a.jpg",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/286658/29/18973/48654/6882f2aeF0891accb/65cd25d3dccbe3fd.jpg",
            "https://img12.360buyimg.com/n2/s480x480_jfs/t1/291750/37/13353/75163/6852e786Fe412de1e/11810bd6d1e66ef3.jpg",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/293586/12/16521/102471/688cf99fF853b12ea/4992b0c84cf45999.jpg",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/292861/27/15626/444919/685a131fFc179d19b/f287b521349f6f77.png",
            "https://img12.360buyimg.com/n2/s480x480_jfs/t1/303834/12/21026/148456/68858cd9F4bedfa27/2146f1a7f8c95c9d.png",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/288090/22/6679/85057/686528e8F10b4ab70/3c0c24c7a6502c7e.jpg",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/306225/1/17246/99492/6870a740F1e6e18cb/6ae79483a5879fd6.jpg",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/312303/4/11500/433222/6857c96bF5ce8145d/4ad9572a938641f2.png",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/286618/1/24406/91258/6880d8a8Fdbfa4432/8336cb455f222899.jpg",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/298146/26/21439/92299/686b7b93F5f2642fa/d65c6c452e259149.jpg",
            "https://img13.360buyimg.com/n2/s480x480_jfs/t1/288135/19/24396/43181/6882f2b3F33cb8df0/dac2da78302fdaab.jpg",
            "https://img12.360buyimg.com/n2/s480x480_jfs/t1/300160/39/21695/49639/68737159F286f0352/1e72cd5e962921f5.png",
            "https://img14.360buyimg.com/n2/s480x480_jfs/t1/265523/33/12420/39995/6788bc4dF9d59ad2a/a83dbc3b57c5dee9.png",
            "https://img13.360buyimg.com/n2/s480x480_jfs/t1/311916/39/14367/100855/68679ae3F14ffc550/cc706025572212dd.jpg",
            "https://img14.360buyimg.com/n2/s480x480_jfs/t1/255237/26/13274/41831/6788bd78F9bc43448/dc08a49346132255.png",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/258405/33/12793/44765/6788bd77F871ea755/749a474cc089dfe7.png",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/95643/12/41106/22080/6788bb0fFbba8c0ad/73673cd3c9482db5.png",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/317725/4/5039/76369/68397a3fF0928f3db/90d9bb178051876a.png",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/314290/40/14668/123788/68690bddF3982d971/c988236adf95f4a2.jpg",
            "https://img10.360buyimg.com/n2/s480x480_jfs/t1/289708/9/10466/70943/6877633cFe9fbb87b/2936d15632ded137.png",
            "https://img11.360buyimg.com/n2/s480x480_jfs/t1/300869/23/20596/66569/686a3a86Fab768c4c/bd65c1b780132286.jpg",
        )

        for ((index, banner) in banners.withIndex()) {
            val url = URI.create(banner).toURL()
            url.openStream().use {
                s3Service.putObject("spu/banner/${index + 1}", it)
            }
        }
    }
}