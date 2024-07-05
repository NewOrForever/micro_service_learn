import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * ClassName:RocketMqCommonUtil
 * Package:PACKAGE_NAME
 * Description: 记录下我遇到的 rocketmq 中间件的一些工具类
 *
 * @Date:2024/7/4 13:58
 * @Author:qs@1.com
 */
public class RocketMqCommonUtil {
    public static void main(String[] args) {
        /**
         * @see org.apache.rocketmq.remoting.common.RemotingHelper
         *  - 主要是一些Socket Address 的转换
         *  - 有个异常信息转成字符串的方法：{@link RemotingHelper#exceptionSimpleDesc(Throwable)} 还不错
         *     - 这个方法只记录了堆栈数组的第一个元素，然后返回异常的简单描述
         *     - 自己可以把这个方法拿来改下，记录所有的堆栈信息 {@link TestExceptionSimpleDesc#exceptionFullDesc(Throwable)}
         */
    }
}
