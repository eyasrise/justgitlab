package com.eyas.framework.sharding;

import com.eyas.framework.SnowflakeIdWorker;
import io.swagger.models.auth.In;
import org.apache.commons.codec.digest.DigestUtils;

public class ShardFieldMerge {

    /**
     * 基因法分片字段合并(id+一个字段---多个不清楚？)
     * 分辨率:如果分8个库每个库8个表--等于64的分辨率相当于2的6次方--2位16进制的就相当于8位2进制
     * --相当于有512种结果完全够分库分表了
     * --将需要合并查询字段进行基因重组，比如一个表用id做分片字段
     * 1、将需要合并的字段进行MD5加密，并取最后两位
     * 2、把md5后的两位数据作为分片字段与id进行合并，存到数据库
     * 3、id查询的时候直接截取后两位去查询
     * 4、另一个条件去查询的时候，直接进行MD5加密取最后两位去查询
     * 5、也可以缓存分片数据
     *
     * @param args
     */
    public static void main(String[] args) {
        Long id = SnowflakeIdWorker.generateId();
        try {
//            String idMd5 = ShardFieldMerge.md52(id+"");
//            String userMd5 = ShardFieldMerge.md52(userName);
//            System.out.println(idMd5);
//            System.out.println(userMd5);
//            String idOne = idMd5.substring(idMd5.length()-2, idMd5.length());
//            System.out.println(idOne);
//            String idTwo = userMd5.substring(userMd5.length()-2, userMd5.length());
//            System.out.println(idTwo);
//            // 进行基因法
//            String newId = id + idTwo;
//            System.out.println(newId);
            // 分库分表就用id后四位去分片

            // 使用id去查询
            String idNew = "70054775755606016023";
            // 截取后两位
            Integer shardingFieldValue = Integer.valueOf(idNew.substring(idNew.length() -2, idNew.length()));
            // 模拟分片
            ShardFieldMerge shardFieldMerge = new ShardFieldMerge();
            shardFieldMerge.fenKuMethodFa(8,64, shardingFieldValue);
//            Integer aa = (shardingFieldValue) % 10;
//            System.out.println(aa);
            // 现在来了一个name
            // 把name进行MD5
            String userName = "王瑞";
            String userMd5 = ShardFieldMerge.md52(userName);
            // 截取后两位
            Integer idTwo = Integer.valueOf(userMd5.substring(userMd5.length()-2, userMd5.length()));
            shardFieldMerge.fenKuMethodFa(8,64, idTwo);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param text 明文
     * @param key 密钥
     * @return 密文
     */
    // 带秘钥加密
    public static String md5(String text, String key) throws Exception {
        // 加密后的字符串
        String md5str = DigestUtils.md5Hex(text + key);
        System.out.println("MD5加密后的字符串为:" + md5str);
        return md5str;
    }

    // 不带秘钥加密
    public static String md52(String text) throws Exception {
        // 加密后的字符串
        String md5str = DigestUtils.md5Hex(text);
        System.out.println("MD52加密后的字符串为:" + md5str + "\t长度：" + md5str.length());
        return md5str;
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param key 密钥
     * @param md5 密文
     */
    // 根据传入的密钥进行验证
    public static boolean verify(String text, String key, String md5) throws Exception {
        String md5str = md5(text, key);
        if (md5str.equalsIgnoreCase(md5)) {
            System.out.println("MD5验证通过");
            return true;
        }
        return false;
    }

    public void fenKuMethodFa(Integer databaseNums, Integer tables, Integer rootKey){
//        // 先计算数据库
//        String databaseNumsBinary = Integer.toBinaryString(databaseNums);
//        String tablesNumBinary = Integer.toBinaryString(tables);
//        String rootKeyBinary = Integer.toBinaryString(rootKey);
//        // 位运算
//        Integer aa = Integer.valueOf(rootKeyBinary) & Integer.valueOf(databaseNumsBinary);
//        System.out.println(aa);
        System.out.println("库:" + rootKey % databaseNums +"---" + "表:" + rootKey % tables);

    }

//    public static void main(String[] args) {
//        ShardFieldMerge shardFieldMerge = new ShardFieldMerge();
//        for (int i=0;i<512; i ++) {
//            shardFieldMerge.fenKuMethodFa(8, 64, i);
//        }
//    }



    public String StringToBinary(String str){
        char[] strChar=str.toCharArray();
        String result="";
        for(int i=0;i<strChar.length;i++){
            result +=Integer.toBinaryString(strChar[i])+ " ";
        }
        return result;
    }


    /**
     * 除法
     * @param a
     * @param b
     * @return
     */
    public static int sub(int a,int b) {
        int res=-1;
        if(a<b){
            return 0;
        }else{
            res=sub(minus(a, b), b)+1;
        }
        return res;
    }

    /**
     * 减法
     * @param a
     * @param b
     * @return
     */
    public static int minus(int a,int b) {
        int B=~(b-1);
        return add(a, B);
    }


    /**
     * 加法
     * @param a
     * @param b
     * @return
     */
    public static int add(int a,int b) {
        int res = a;
        int xor = a ^ b;//得到原位和
        int forward = (a & b) << 1;//得到进位和
        if (forward != 0) {//若进位和不为0，则递归求原位和+进位和
            res = add(xor, forward);
        } else {
            res = xor;//若进位和为0，则此时原位和为所求和
        }
        return res;
    }
}
