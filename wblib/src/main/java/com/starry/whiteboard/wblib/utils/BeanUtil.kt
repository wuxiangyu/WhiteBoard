package com.starry.whiteboard.wblib.utils

/**
 * Created by gpy on 2015/8/13.
 */
object BeanUtil {
    /**
     * @return Object
     * <p/>
     * 用到反射机制
     * <p/>
     * 此方法将调用obj1的getter方法，将得到的值作为相应的参数传给obj2的setter方法
     * <p/>
     * 注意，obj1的getter方法和obj2方法必须是public类型
     * @parameter Object obj1,Object obj2
     */
    @JvmStatic
    @Throws(Exception::class)
    fun CopyBeanToBean(obj1: Any, obj2: Any): Any {
        val methods1 = obj1.javaClass.methods
        val methods2 = obj2.javaClass.methods

        for (m1 in methods1) {
            val name1 = m1.name
            var suffix1 = ""
            if (name1.startsWith("get")) {
                suffix1 = name1.substring(3)
            } else if (name1.startsWith("is")) {
                suffix1 = name1.substring(2)
            }
            if (suffix1.isNotEmpty()) {
                for (m2 in methods2) {
                    val name2 = m2.name
                    if (name2.startsWith("set")) {
                        val suffix2 = name2.substring(3)
                        if (suffix1 == suffix2) {
                            try {
                                val value = m1.invoke(obj1)
                                m2.invoke(obj2, value)
                            } catch (e: Exception) {
                                // Ignore invocation errors
                            }
                        }
                    }
                }
            }
        }
        return obj2
    }
}
