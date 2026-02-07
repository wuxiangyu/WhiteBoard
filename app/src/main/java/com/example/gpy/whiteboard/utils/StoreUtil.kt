package com.example.gpy.whiteboard.utils

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.text.TextUtils
import android.util.Log
import com.example.gpy.whiteboard.R
import com.github.guanpy.wblib.bean.*
import com.github.guanpy.wblib.utils.AppContextUtil
import com.github.guanpy.wblib.utils.OperationUtils
import com.google.gson.Gson
import java.io.*
import java.util.*

/**
 * 存储空间工具类
 * Created by gpy on 2015/8/2.
 */
object StoreUtil {
    private const val TAG = "StoreUtil"
    private const val CHARSET = "UTF-8"
    const val CACHE_DIR = "WhiteBoard"
    private const val CACHE_DIR_PHOTO = "photo"
    private const val PHOTO_FORMAT_PNG = ".png"
    private const val CACHE_DIR_WB = "wb"
    private const val WB_FORMAT = ".wb"

    /**
     * 获取保存路径
     */
    @JvmStatic
    val photoSavePath: String
        get() = photoPath + File.separator + UUID.randomUUID().toString() + PHOTO_FORMAT_PNG

    @JvmStatic
    val photoPath: String
        get() = SdCardStatus.getDefaulstCacheDirInSdCard() + File.separator + CACHE_DIR_PHOTO

    /**
     * 获取保存路径
     */
    @JvmStatic
    val wbSavePath: String
        get() = wbPath + File.separator + UUID.randomUUID().toString() + WB_FORMAT

    @JvmStatic
    val wbPath: String
        get() = SdCardStatus.getDefaulstCacheDirInSdCard() + File.separator + CACHE_DIR_WB

    /**
     * 存储白板集
     */
    @JvmStatic
    fun saveWhiteBoardPoints() {
        val whiteBoardPoints = OperationUtils.getInstance().getWhiteBoardPoints()
        if (whiteBoardPoints?.whiteBoardPoints == null || whiteBoardPoints.whiteBoardPoints!!.isEmpty()) {
            return
        }
        for (whiteBoardPoint in whiteBoardPoints.whiteBoardPoints!!) { //清除绘画路径，保留字符串形式就行
            for (drawPoint in whiteBoardPoint.savePoints) {
                if (drawPoint.type == OperationUtils.DRAW_PEN) {
                    drawPoint.drawPen = null
                }
            }
        }
        val strJson = Gson().toJson(whiteBoardPoints)
        write(strJson, wbSavePath)
        convertWhiteBoardPoints(whiteBoardPoints)
        OperationUtils.getInstance().setWhiteBoardPoints(whiteBoardPoints)
        ToastUtils.showToast(
            AppContextUtil.getContext()!!,
            AppContextUtil.getContext()!!.getString(R.string.white_board_save_sucess)
        )
    }

    /**
     * 读取白板集
     */
    @JvmStatic
    fun readWhiteBoardPoints(filePath: String?) {
        val strJson = read(filePath)
        if (!TextUtils.isEmpty(strJson)) {
            val whiteBoardPoints = Gson().fromJson(strJson, WhiteBoardPoints::class.java)
            convertWhiteBoardPoints(whiteBoardPoints)
            OperationUtils.getInstance().setWhiteBoardPoints(whiteBoardPoints)
        }
    }

    /**
     * 从json字符中将Path、Paint系统类转换出来
     */
    @JvmStatic
    fun convertWhiteBoardPoints(whiteBoardPoints: WhiteBoardPoints) {
        for (whiteBoardPoint in whiteBoardPoints.whiteBoardPoints!!) {
            whiteBoardPoint.deletePoints.clear()
            for (drawPoint in whiteBoardPoint.savePoints) {
                if (drawPoint.type == OperationUtils.DRAW_PEN) {
                    val drawPenStr = drawPoint.drawPenStr!! // Should not be null if logic is correct
                    val paint = Paint()
                    paint.isAntiAlias = true //是否使用抗锯齿功能,会消耗较大资源，绘制图形速度会变慢
                    paint.isDither = true // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
                    paint.color = drawPenStr.color //设置绘制的颜色
                    paint.style = Paint.Style.STROKE //设置画笔的样式
                    paint.strokeJoin = Paint.Join.ROUND //设置绘制时各图形的结合方式，如平滑效果等
                    paint.strokeCap =
                        Paint.Cap.ROUND //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式    Cap.ROUND,或方形样式Cap.SQUARE
                    paint.strokeWidth =
                        drawPenStr.strokeWidth //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度
                    if (drawPenStr.isEraser) {
                        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) //擦除模式
                    }
                    val path = Path()
                    path.moveTo(drawPenStr.moveTo!!.x, drawPenStr.moveTo!!.y)
                    for (i in 0 until drawPenStr.quadToA!!.size) {
                        val pointA = drawPenStr.quadToA!![i]
                        val pointB = drawPenStr.quadToB!![i]
                        path.quadTo(pointA.x, pointA.y, pointB.x, pointB.y)
                    }
                    path.lineTo(drawPenStr.lineTo!!.x, drawPenStr.lineTo!!.y)
                    path.offset(drawPenStr.offset!!.x, drawPenStr.offset!!.y)

                    val drawPenPoint = DrawPenPoint()
                    drawPenPoint.paint = paint
                    drawPenPoint.path = path
                    drawPoint.drawPen = drawPenPoint
                }
            }
        }
    }

    /**
     * 保存内容到文件中
     */
    @JvmStatic
    fun write(strWb: String?, path: String?) {
        if (TextUtils.isEmpty(strWb) || TextUtils.isEmpty(path)) {
            Log.d(TAG, "Trying to save null or 0 length strWb or path")
            return
        }
        var toFile: File? = File(path!!)
        if (!toFile!!.parentFile.exists()) {
            toFile.parentFile.mkdirs()
        }
        if (toFile.exists()) {
            toFile.delete()
        }
        try {
            toFile.createNewFile()
        } catch (e: IOException) {
            Log.e(TAG, "IOException：" + e.message)
            toFile = null
        } finally {
            if (null != toFile && null != strWb) {
                var outStream: OutputStream? = null
                try {
                    outStream = FileOutputStream(toFile)
                } catch (e: FileNotFoundException) {
                    Log.d(TAG, "FileNotFoundException：" + e.message)
                    outStream = null
                } finally {
                    if (null != outStream) {
                        try {
                            outStream.write(strWb.toByteArray(charset("utf-8")))
                            outStream.flush()
                        } catch (e: IOException) {
                            Log.e(TAG, "IOException：" + e.message)
                        } finally {
                            try {
                                outStream.close()
                            } catch (e: IOException) {
                                Log.d(TAG, "IOException" + e.message)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 加载文件内容
     */
    @JvmStatic
    fun read(wbPath: String?): String? {
        if (wbPath == null) return null
        val file = File(wbPath)
        if (file.exists()) {
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(file)
                val len = fis.available()
                if (len > 0) {
                    val buf = ByteArray(len)
                    fis.read(buf)
                    return String(buf, charset(CHARSET))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            } finally {
                if (fis != null) try {
                    fis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}
