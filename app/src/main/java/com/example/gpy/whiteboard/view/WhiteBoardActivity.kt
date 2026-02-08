package com.example.gpy.whiteboard.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import com.example.gpy.whiteboard.R
import com.example.gpy.whiteboard.utils.StoreUtil
import com.example.gpy.whiteboard.view.base.BaseActivity
import com.example.gpy.whiteboard.view.widget.floatingactionmenu.FloatingActionsMenu
import com.example.gpy.whiteboard.view.widget.floatingactionmenu.FloatingImageButton
import com.github.guanpy.library.ann.ReceiveEvents
import com.github.guanpy.wblib.utils.Events
import com.github.guanpy.wblib.utils.OperationUtils
import com.github.guanpy.wblib.utils.WhiteBoardVariable
import com.github.guanpy.wblib.widget.DrawPenView
import com.github.guanpy.wblib.widget.DrawTextLayout
import com.github.guanpy.wblib.widget.DrawTextView
import java.io.File
import java.io.FileOutputStream
import com.example.gpy.whiteboard.view.fragment.EmojiBSFragment
import com.github.guanpy.wblib.bean.DrawEmojiPoint
import com.github.guanpy.wblib.bean.DrawPoint
import com.github.guanpy.wblib.widget.DrawEmojiLayout
import com.github.guanpy.wblib.widget.DrawEmojiView

/**
 * 白板工具
 * Created by gpy on 2015/6/2.
 */
class WhiteBoardActivity : BaseActivity(), View.OnClickListener, EmojiBSFragment.EmojiListener, DrawEmojiLayout.OnEmojiActionListener {
    private var mEditingEmojiPoint: DrawEmojiPoint? = null
    private lateinit var mIvWhiteBoardBook: ImageView
    private lateinit var mRlHead: RelativeLayout
    private lateinit var mIvWhiteBoardBack: ImageView
    private lateinit var mTvWhiteBoardHead: TextView
    private lateinit var mIvWhiteBoardExport: ImageView
    private lateinit var mIvWhiteBoardSave: ImageView
    private lateinit var mFlView: FrameLayout
    private lateinit var mDbView: DrawPenView
    private lateinit var mDeView: DrawEmojiLayout
    private lateinit var mDtView: DrawTextLayout
    private lateinit var mFabMenuSize: FloatingActionsMenu
    private lateinit var mBtSizeLarge: FloatingImageButton
    private lateinit var mBtSizeMiddle: FloatingImageButton
    private lateinit var mBtSizeMini: FloatingImageButton
    private lateinit var mFabMenuColor: FloatingActionsMenu
    private lateinit var mBtColorGreen: FloatingImageButton
    private lateinit var mBtColorPurple: FloatingImageButton
    private lateinit var mBtColorPink: FloatingImageButton
    private lateinit var mBtColorOrange: FloatingImageButton
    private lateinit var mBtColorBlack: FloatingImageButton
    private lateinit var mFabMenuText: FloatingActionsMenu
    private lateinit var mBtTextUnderline: FloatingImageButton
    // private lateinit var mBtTextEmoji: FloatingImageButton // Removed
    private lateinit var mBtTextItalics: FloatingImageButton
    private lateinit var mBtTextBold: FloatingImageButton
    private lateinit var mIvWhiteBoardEmoji: ImageView // Added top-level button
    private lateinit var mFabMenuEraser: FloatingActionsMenu
    private lateinit var mBtEraserLarge: FloatingImageButton
    private lateinit var mBtEraserMiddle: FloatingImageButton
    private lateinit var mBtEraserMini: FloatingImageButton
    private lateinit var mIvWhiteBoardUndo: ImageView
    private lateinit var mIvWhiteBoardRedo: ImageView


    private lateinit var mLlWhiteBoardPage: LinearLayout
    private lateinit var mIvWhiteBoardPre: ImageView
    private lateinit var mTvWhiteBoardPage: TextView
    private lateinit var mIvWhiteBoardNext: ImageView
    private lateinit var mIvWhiteBoardAdd: ImageView
    private lateinit var mIvWhiteBoardDisable: ImageView
    private lateinit var mIvWhiteBoardQuit: ImageView
    private lateinit var mIvWhiteBoardConfirm: ImageView
    private lateinit var mRlContent: RelativeLayout
    private lateinit var mRlBottom: RelativeLayout
    private lateinit var mVBottomBack: View
    private lateinit var mLlWhiteBoardPre: LinearLayout
    private lateinit var mLlWhiteBoardNext: LinearLayout

    override val layoutId: Int
        get() = R.layout.activity_white_board

    override fun afterCreate(bundle: Bundle?) {
        initView()
        initEvent()
    }

    private fun initView() {
        mIvWhiteBoardBook = findViewById(R.id.iv_white_board_book)
        mRlHead = findViewById(R.id.rl_head)
        mIvWhiteBoardBack = findViewById(R.id.iv_white_board_back)
        mTvWhiteBoardHead = findViewById(R.id.tv_white_board_head)
        mIvWhiteBoardExport = findViewById(R.id.iv_white_board_export)
        mIvWhiteBoardSave = findViewById(R.id.iv_white_board_save)
        mFlView = findViewById(R.id.fl_view)
        mDbView = findViewById(R.id.db_view)
        mDeView = findViewById(R.id.de_view)
        mDtView = findViewById(R.id.dt_view)
        mFabMenuSize = findViewById(R.id.fab_menu_size)
        mBtSizeLarge = findViewById(R.id.bt_size_large)
        mBtSizeMiddle = findViewById(R.id.bt_size_middle)
        mBtSizeMini = findViewById(R.id.bt_size_mini)
        mFabMenuColor = findViewById(R.id.fab_menu_color)
        mBtColorGreen = findViewById(R.id.bt_color_green)
        mBtColorPurple = findViewById(R.id.bt_color_purple)
        mBtColorPink = findViewById(R.id.bt_color_pink)
        mBtColorOrange = findViewById(R.id.bt_color_orange)
        mBtColorBlack = findViewById(R.id.bt_color_black)
        mFabMenuText = findViewById(R.id.fab_menu_text)
        mBtTextUnderline = findViewById(R.id.bt_text_underline)
        // mBtTextEmoji = findViewById(R.id.bt_text_emoji)
        mBtTextItalics = findViewById(R.id.bt_text_italics)
        mBtTextBold = findViewById(R.id.bt_text_bold)
        mIvWhiteBoardEmoji = findViewById(R.id.iv_white_board_emoji) // Added
        mFabMenuEraser = findViewById(R.id.fab_menu_eraser)
        mBtEraserLarge = findViewById(R.id.bt_eraser_large)
        mBtEraserMiddle = findViewById(R.id.bt_eraser_middle)
        mBtEraserMini = findViewById(R.id.bt_eraser_mini)
        mIvWhiteBoardUndo = findViewById(R.id.iv_white_board_undo)
        mIvWhiteBoardRedo = findViewById(R.id.iv_white_board_redo)
        mLlWhiteBoardPage = findViewById(R.id.ll_white_board_page)
        mIvWhiteBoardPre = findViewById(R.id.iv_white_board_pre)
        mTvWhiteBoardPage = findViewById(R.id.tv_white_board_page)
        mIvWhiteBoardNext = findViewById(R.id.iv_white_board_next)
        mIvWhiteBoardAdd = findViewById(R.id.iv_white_board_add)
        mIvWhiteBoardDisable = findViewById(R.id.iv_white_board_disable)
        mIvWhiteBoardQuit = findViewById(R.id.iv_white_board_quit)
        mIvWhiteBoardConfirm = findViewById(R.id.iv_white_board_confirm)
        mRlContent = findViewById(R.id.rl_content)
        mRlBottom = findViewById(R.id.rl_bottom)
        mVBottomBack = findViewById(R.id.v_bottom_back)
        mLlWhiteBoardPre = findViewById(R.id.ll_white_board_pre)
        mLlWhiteBoardNext = findViewById(R.id.ll_white_board_next)
        
        mDtView.init(this)
        mDeView.init(this) // keep existing init if it was there? No, looking at initView: mDeView = findViewById...
        mDeView.setOnEmojiActionListener(this)
        mDtView.init(this)
        val keepPoints = intent.getBooleanExtra("KEEP_POINTS", false)
        OperationUtils.init(keepPoints) // Ensure defaults (DISABLE=true)
        if (keepPoints) {
            // Do not disable drawing.
            // OperationUtils.DISABLE = true // already set in init()
        }
        changePenBack()
        changeColorBack()
        changeEraserBack()
        toolsOperation(WhiteBoardVariable.Operation.PEN_NORMAL)
        mDbView.post { showPoints() }
    }

    private fun initEvent() {
        mIvWhiteBoardEmoji.bringToFront()
        mIvWhiteBoardEmoji.setOnClickListener(this)

        //头部
        mIvWhiteBoardBack.setOnClickListener(this)
        mIvWhiteBoardExport.setOnClickListener(this)
        mIvWhiteBoardSave.setOnClickListener(this)
        mIvWhiteBoardQuit.setOnClickListener(this)
        mIvWhiteBoardConfirm.setOnClickListener(this)
        mVBottomBack.setOnClickListener(this)
        //画笔尺寸大小
        mFabMenuSize.setOnFloatingActionsMenuClickListener(object : FloatingActionsMenu.OnFloatingActionsMenuClickListener {
            override fun addButtonLister() {
                toolsOperation(WhiteBoardVariable.Operation.PEN_CLICK)
            }
        })
        mBtSizeLarge.setOnClickListener(this)
        mBtSizeMiddle.setOnClickListener(this)
        mBtSizeMini.setOnClickListener(this)
        //画笔或者文字颜色
        mFabMenuColor.setOnFloatingActionsMenuClickListener(object : FloatingActionsMenu.OnFloatingActionsMenuClickListener {
            override fun addButtonLister() {
                toolsOperation(WhiteBoardVariable.Operation.COLOR_CLICK)
            }
        })
        mBtColorGreen.setOnClickListener(this)
        mBtColorPurple.setOnClickListener(this)
        mBtColorPink.setOnClickListener(this)
        mBtColorOrange.setOnClickListener(this)
        mBtColorBlack.setOnClickListener(this)
        //文字样式
        mFabMenuText.setOnFloatingActionsMenuClickListener(object : FloatingActionsMenu.OnFloatingActionsMenuClickListener {
            override fun addButtonLister() {
                toolsOperation(WhiteBoardVariable.Operation.TEXT_CLICK)
            }
        })
        mBtTextUnderline.setOnClickListener(this)
        // mBtTextEmoji.setOnClickListener(this)
        mBtTextItalics.setOnClickListener(this)
        mBtTextBold.setOnClickListener(this)
        //橡皮擦尺寸大小
        mFabMenuEraser.setOnFloatingActionsMenuClickListener(object : FloatingActionsMenu.OnFloatingActionsMenuClickListener {
            override fun addButtonLister() {
                toolsOperation(WhiteBoardVariable.Operation.ERASER_CLICK)
            }
        })
        mBtEraserLarge.setOnClickListener(this)
        mBtEraserMiddle.setOnClickListener(this)
        mBtEraserMini.setOnClickListener(this)

        mIvWhiteBoardUndo.setOnClickListener(this)
        mIvWhiteBoardRedo.setOnClickListener(this)

        mLlWhiteBoardPre.setOnClickListener(this)
        mIvWhiteBoardPre.setOnClickListener(this)
        mLlWhiteBoardNext.setOnClickListener(this)
        mIvWhiteBoardNext.setOnClickListener(this)
        mIvWhiteBoardAdd.setOnClickListener(this)
        mIvWhiteBoardDisable.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_white_board_back) {
            onBackPressed()
        } else if (id == R.id.iv_white_board_quit) {
            afterEdit(false)
        } else if (id == R.id.iv_white_board_confirm) {
            afterEdit(true)
        } else if (id == R.id.iv_white_board_export) {
            toolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK)
            StoreUtil.saveWhiteBoardPoints()
        } else if (id == R.id.iv_white_board_save) {
            toolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK)
            saveImage()
        } else if (id == R.id.v_bottom_back) {
            toolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK)
        } else if (id == R.id.bt_size_large) {
            setPenSize(WhiteBoardVariable.PenSize.LARRGE)
        } else if (id == R.id.bt_size_middle) {
            setPenSize(WhiteBoardVariable.PenSize.MIDDLE)
        } else if (id == R.id.bt_size_mini) {
            setPenSize(WhiteBoardVariable.PenSize.MINI)
        } else if (id == R.id.bt_color_green) {
            setColor(WhiteBoardVariable.Color.GREEN)
        } else if (id == R.id.bt_color_purple) {
            setColor(WhiteBoardVariable.Color.PURPLE)
        } else if (id == R.id.bt_color_pink) {
            setColor(WhiteBoardVariable.Color.PINK)
        } else if (id == R.id.bt_color_orange) {
            setColor(WhiteBoardVariable.Color.ORANGE)
        } else if (id == R.id.bt_color_black) {
            setColor(WhiteBoardVariable.Color.BLACK)
        } else if (id == R.id.bt_text_underline) {
            setTextStyle(WhiteBoardVariable.TextStyle.CHANGE_UNDERLINE)
        } else if (id == R.id.iv_white_board_emoji) {
            val emojiBSFragment = EmojiBSFragment()
            emojiBSFragment.setEmojiListener(this)
            emojiBSFragment.show(supportFragmentManager, emojiBSFragment.tag)
        } else if (id == R.id.bt_text_italics) {
            setTextStyle(WhiteBoardVariable.TextStyle.CHANGE_ITALICS)
        } else if (id == R.id.bt_text_bold) {
            setTextStyle(WhiteBoardVariable.TextStyle.CHANGE_BOLD)
        } else if (id == R.id.bt_eraser_large) {
            setEraserSize(WhiteBoardVariable.EraserSize.LARRGE)
        } else if (id == R.id.bt_eraser_middle) {
            setEraserSize(WhiteBoardVariable.EraserSize.MIDDLE)
        } else if (id == R.id.bt_eraser_mini) {
            setEraserSize(WhiteBoardVariable.EraserSize.MINI)
        } else if (id == R.id.iv_white_board_undo) {
            toolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK)
            if (OperationUtils.DISABLE) {
                undo()
            }
        } else if (id == R.id.iv_white_board_redo) {
            toolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK)
            if (OperationUtils.DISABLE) {
                redo()
            }
        } else if (id == R.id.ll_white_board_pre || id == R.id.iv_white_board_pre) {
            toolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK)
            prePage()
        } else if (id == R.id.ll_white_board_next || id == R.id.iv_white_board_next) {
            toolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK)
            nextPage()
        } else if (id == R.id.iv_white_board_add) {
            toolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK)
            newPage()
        } else if (id == R.id.iv_white_board_disable) {
            toolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK)
            if (OperationUtils.DISABLE) {
                OperationUtils.DISABLE = false
                mIvWhiteBoardDisable.setImageResource(R.drawable.white_board_undisable_selector)
                mRlBottom.visibility = View.GONE
            } else {
                OperationUtils.DISABLE = true
                mIvWhiteBoardDisable.setImageResource(R.drawable.white_board_disable_selector)
                mRlBottom.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 设置画笔尺寸
     */
    private fun setPenSize(size: Int) {
        OperationUtils.mCurrentPenSize = size
        changePenBack()
        mDbView.setPenSize()
    }

    /**
     * 切换画笔尺寸按按钮背景
     */
    private fun changePenBack() {
        if (OperationUtils.mCurrentPenSize == WhiteBoardVariable.PenSize.LARRGE) {
            mBtSizeLarge.drawCircleAndRing(WhiteBoardVariable.PenSize.LARRGE, OperationUtils.mCurrentColor)
            mBtSizeMiddle.drawCircle(WhiteBoardVariable.PenSize.MIDDLE)
            mBtSizeMini.drawCircle(WhiteBoardVariable.PenSize.MINI)
        } else if (OperationUtils.mCurrentPenSize == WhiteBoardVariable.PenSize.MIDDLE) {
            mBtSizeLarge.drawCircle(WhiteBoardVariable.PenSize.LARRGE)
            mBtSizeMiddle.drawCircleAndRing(WhiteBoardVariable.PenSize.MIDDLE, OperationUtils.mCurrentColor)
            mBtSizeMini.drawCircle(WhiteBoardVariable.PenSize.MINI)
        } else if (OperationUtils.mCurrentPenSize == WhiteBoardVariable.PenSize.MINI) {
            mBtSizeLarge.drawCircle(WhiteBoardVariable.PenSize.LARRGE)
            mBtSizeMiddle.drawCircle(WhiteBoardVariable.PenSize.MIDDLE)
            mBtSizeMini.drawCircleAndRing(WhiteBoardVariable.PenSize.MINI, OperationUtils.mCurrentColor)
        }
    }

    /**
     * 设置颜色
     */
    private fun setColor(color: Int) {
        OperationUtils.mCurrentColor = color
        changeColorBack()
        setPenSize(OperationUtils.mCurrentPenSize)
        mDbView.setPenColor()
        mDtView.setTextColor()
    }

    /**
     * 切换颜色控制按钮背景
     */
    private fun changeColorBack() {
        when (OperationUtils.mCurrentColor) {
            WhiteBoardVariable.Color.BLACK -> mFabMenuColor.setAddButtonBackground(R.drawable.white_board_color_black_selector)
            WhiteBoardVariable.Color.ORANGE -> mFabMenuColor.setAddButtonBackground(R.drawable.white_board_color_orange_selector)
            WhiteBoardVariable.Color.PINK -> mFabMenuColor.setAddButtonBackground(R.drawable.white_board_color_pink_selector)
            WhiteBoardVariable.Color.PURPLE -> mFabMenuColor.setAddButtonBackground(R.drawable.white_board_color_purple_selector)
            WhiteBoardVariable.Color.GREEN -> mFabMenuColor.setAddButtonBackground(R.drawable.white_board_color_green_selector)
        }
    }

    /**
     * 设置文字风格
     */
    private fun setTextStyle(textStyle: Int) {
        mDtView.setTextStyle(textStyle)
        changeTextBack()
    }

    /**
     * 切换文字相关按钮背景
     */
    private fun changeTextBack() {
        val size = OperationUtils.savePoints.size
        if (size < 1) {
            return
        }
        val dp = OperationUtils.savePoints[size - 1]
        if (dp.type != OperationUtils.DRAW_TEXT) {
            return
        }
        if (dp.drawText!!.isUnderline) {
            mBtTextUnderline.setBackgroundResource(R.drawable.white_board_text_underline_selected_selector)
        } else {
            mBtTextUnderline.setBackgroundResource(R.drawable.white_board_text_underline_selector)
        }

        if (dp.drawText!!.isItalics) {
            mBtTextItalics.setBackgroundResource(R.drawable.white_board_text_italics_selected_selector)
        } else {
            mBtTextItalics.setBackgroundResource(R.drawable.white_board_text_italics_selector)
        }

        if (dp.drawText!!.isBold) {
            mBtTextBold.setBackgroundResource(R.drawable.white_board_text_bold_selected_selector)
        } else {
            mBtTextBold.setBackgroundResource(R.drawable.white_board_text_bold_selector)
        }
    }

    /**
     * 设置橡皮擦尺寸
     */
    private fun setEraserSize(size: Int) {
        OperationUtils.mCurrentEraserSize = size
        changeEraserBack()
        mDbView.setEraserSize()
    }

    /**
     * 切换橡皮擦尺寸按钮背景
     */
    private fun changeEraserBack() {
        when (OperationUtils.mCurrentEraserSize) {
            WhiteBoardVariable.EraserSize.LARRGE -> {
                mBtEraserLarge.drawCircleAndRing(WhiteBoardVariable.EraserSize.LARRGE, WhiteBoardVariable.Color.BLACK)
                mBtEraserMiddle.drawCircle(WhiteBoardVariable.EraserSize.MIDDLE, WhiteBoardVariable.Color.BLACK)
                mBtEraserMini.drawCircle(WhiteBoardVariable.EraserSize.MINI, WhiteBoardVariable.Color.BLACK)
            }
            WhiteBoardVariable.EraserSize.MIDDLE -> {
                mBtEraserLarge.drawCircle(WhiteBoardVariable.EraserSize.LARRGE, WhiteBoardVariable.Color.BLACK)
                mBtEraserMiddle.drawCircleAndRing(WhiteBoardVariable.EraserSize.MIDDLE, WhiteBoardVariable.Color.BLACK)
                mBtEraserMini.drawCircle(WhiteBoardVariable.EraserSize.MINI, WhiteBoardVariable.Color.BLACK)
            }
            WhiteBoardVariable.EraserSize.MINI -> {
                mBtEraserLarge.drawCircle(WhiteBoardVariable.EraserSize.LARRGE, WhiteBoardVariable.Color.BLACK)
                mBtEraserMiddle.drawCircle(WhiteBoardVariable.EraserSize.MIDDLE, WhiteBoardVariable.Color.BLACK)
                mBtEraserMini.drawCircleAndRing(WhiteBoardVariable.EraserSize.MINI, WhiteBoardVariable.Color.BLACK)
            }
        }
    }

    /**
     * 新建白板
     */
    private fun newPage() {
        OperationUtils.newPage()
        showPoints()
    }

    /**
     * 上一页
     */
    private fun prePage() {
        if (OperationUtils.mCurrentIndex > 0) {
            OperationUtils.mCurrentIndex--
            showPoints()
        }
    }

    /**
     * 下一页
     */
    private fun nextPage() {
        if (OperationUtils.mCurrentIndex + 1 < OperationUtils.drawPointSize) {
            OperationUtils.mCurrentIndex++
            showPoints()
        }
    }

    /**
     * 重新显示白板
     */
    private fun showPoints() {
        mDbView.showPoints()
        mDeView.showPoints()
        mDtView.showPoints()
        mTvWhiteBoardPage.text = "" + (OperationUtils.mCurrentIndex + 1) + "/" + OperationUtils.drawPointSize
        showPage()
        showUndoRedo()
    }

    /**
     * 显示上下页是否可点击
     */
    private fun showPage() {
        if (OperationUtils.mCurrentIndex + 1 == OperationUtils.drawPointSize) {
            mIvWhiteBoardNext.setImageResource(R.drawable.white_board_next_page_click)
        } else {
            mIvWhiteBoardNext.setImageResource(R.drawable.white_board_next_page_selector)
        }
        if (OperationUtils.mCurrentIndex == 0) {
            mIvWhiteBoardPre.setImageResource(R.drawable.white_board_pre_page_click)
        } else {
            mIvWhiteBoardPre.setImageResource(R.drawable.white_board_pre_page_selector)
        }
    }

    /**
     * 撤销
     */
    private fun undo() {
        val size = OperationUtils.savePoints.size
        if (size == 0) {
            return
        } else {
            OperationUtils.deletePoints.add(OperationUtils.savePoints[size - 1])
            OperationUtils.savePoints.removeAt(size - 1)
            val delSize = OperationUtils.deletePoints.size
            if (OperationUtils.deletePoints[delSize - 1].type == OperationUtils.DRAW_PEN) {
                mDbView.undo()
            } else if (OperationUtils.deletePoints[delSize - 1].type == OperationUtils.DRAW_TEXT) {
                mDtView.undo()
            } else if (OperationUtils.deletePoints[delSize - 1].type == OperationUtils.DRAW_EMOJI) {
                mDeView.undo()
            }
            showUndoRedo()
        }
    }

    /**
     * 重做
     */
    private fun redo() {
        val size = OperationUtils.deletePoints.size
        if (size == 0) {
            return
        } else {
            OperationUtils.savePoints.add(OperationUtils.deletePoints[size - 1])
            OperationUtils.deletePoints.removeAt(size - 1)
            val saveSize = OperationUtils.savePoints.size
            if (OperationUtils.savePoints[saveSize - 1].type == OperationUtils.DRAW_PEN) {
                mDbView.redo()
            } else if (OperationUtils.savePoints[saveSize - 1].type == OperationUtils.DRAW_TEXT) {
                mDtView.redo()
            } else if (OperationUtils.savePoints[saveSize - 1].type == OperationUtils.DRAW_EMOJI) {
                mDeView.redo()
            }
            showUndoRedo()
        }
    }

    /**
     * 文字编辑之后
     */
    private fun afterEdit(isSave: Boolean) {
        mIvWhiteBoardBack.visibility = View.VISIBLE
        mIvWhiteBoardExport.visibility = View.VISIBLE
        mIvWhiteBoardSave.visibility = View.VISIBLE
        mRlBottom.visibility = View.VISIBLE
        mIvWhiteBoardDisable.visibility = View.VISIBLE
        mIvWhiteBoardQuit.visibility = View.GONE
        mIvWhiteBoardConfirm.visibility = View.GONE
        mDbView.showPoints()
        mDeView.showPoints()
        mDtView.afterEdit(isSave)
    }

    /**
     * 白板工具栏点击切换操作
     */
    private fun toolsOperation(currentOperation: Int) {
        setPenOperation(currentOperation)
        setColorOperation(currentOperation)
        setTextOperation(currentOperation)
        setEraserOperation(currentOperation)
        showOutSideView()
    }

    /**
     * 显示挡板
     */
    private fun showOutSideView() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (OperationUtils.mCurrentOPerationPen == WhiteBoardVariable.Operation.PEN_EXPAND
                || OperationUtils.mCurrentOPerationColor == WhiteBoardVariable.Operation.COLOR_EXPAND
                || OperationUtils.mCurrentOPerationText == WhiteBoardVariable.Operation.TEXT_EXPAND
                || OperationUtils.mCurrentOPerationEraser == WhiteBoardVariable.Operation.ERASER_EXPAND
            ) {
                mVBottomBack.visibility = View.VISIBLE
            } else {
                mVBottomBack.visibility = View.GONE
            }
        }, 100)
    }

    /**
     * 白板工具栏点击切换操作-画笔
     */
    private fun setPenOperation(currentOperation: Int) {
        when (currentOperation) {
            WhiteBoardVariable.Operation.PEN_CLICK -> when (OperationUtils.mCurrentOPerationPen) {
                WhiteBoardVariable.Operation.PEN_NORMAL -> {
                    OperationUtils.mCurrentDrawType = OperationUtils.DRAW_PEN
                    mDbView.setPaint(null)
                    mFabMenuSize.setAddButtonBackground(R.drawable.white_board_pen_selected_selector)
                    OperationUtils.mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_CLICK
                }
                WhiteBoardVariable.Operation.PEN_CLICK -> {
                    mFabMenuSize.expand()
                    changePenBack()
                    OperationUtils.mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_EXPAND
                }
                WhiteBoardVariable.Operation.PEN_EXPAND -> {
                    mFabMenuSize.collapse()
                    OperationUtils.mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_CLICK
                }
            }
            WhiteBoardVariable.Operation.TEXT_CLICK, WhiteBoardVariable.Operation.ERASER_CLICK -> when (OperationUtils.mCurrentOPerationPen) {
                WhiteBoardVariable.Operation.PEN_NORMAL -> {
                }
                WhiteBoardVariable.Operation.PEN_CLICK -> {
                    mFabMenuSize.clearDraw()
                    mFabMenuSize.setAddButtonBackground(R.drawable.white_board_pen_selector)
                    OperationUtils.mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_NORMAL
                }
                WhiteBoardVariable.Operation.PEN_EXPAND -> {
                    mFabMenuSize.collapse()
                    mFabMenuSize.clearDraw()
                    mFabMenuSize.setAddButtonBackground(R.drawable.white_board_pen_selector)
                    OperationUtils.mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_NORMAL
                }
            }
            WhiteBoardVariable.Operation.COLOR_CLICK, WhiteBoardVariable.Operation.OUTSIDE_CLICK -> when (OperationUtils.mCurrentOPerationPen) {
                WhiteBoardVariable.Operation.PEN_NORMAL -> {
                }
                WhiteBoardVariable.Operation.PEN_CLICK -> {
                }
                WhiteBoardVariable.Operation.PEN_EXPAND -> {
                    mFabMenuSize.collapse()
                    OperationUtils.mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_CLICK
                }
            }
        }
    }

    /**
     * 白板工具栏点击切换操作-颜色
     */
    private fun setColorOperation(currentOperation: Int) {
        when (currentOperation) {
            WhiteBoardVariable.Operation.PEN_CLICK, WhiteBoardVariable.Operation.TEXT_CLICK, WhiteBoardVariable.Operation.ERASER_CLICK, WhiteBoardVariable.Operation.OUTSIDE_CLICK -> when (OperationUtils.mCurrentOPerationColor) {
                WhiteBoardVariable.Operation.COLOR_NORMAL -> {
                }
                WhiteBoardVariable.Operation.COLOR_EXPAND -> {
                    mFabMenuColor.collapse()
                    OperationUtils.mCurrentOPerationColor = WhiteBoardVariable.Operation.COLOR_NORMAL
                }
            }
            WhiteBoardVariable.Operation.COLOR_CLICK -> when (OperationUtils.mCurrentOPerationColor) {
                WhiteBoardVariable.Operation.COLOR_NORMAL -> {
                    mFabMenuColor.expand()
                    OperationUtils.mCurrentOPerationColor = WhiteBoardVariable.Operation.COLOR_EXPAND
                }
                WhiteBoardVariable.Operation.COLOR_EXPAND -> {
                    mFabMenuColor.collapse()
                    OperationUtils.mCurrentOPerationColor = WhiteBoardVariable.Operation.COLOR_NORMAL
                }
            }
        }
    }

    /**
     * 白板工具栏点击切换操作-文字
     */
    private fun setTextOperation(currentOperation: Int) {
        when (currentOperation) {
            WhiteBoardVariable.Operation.TEXT_CLICK -> when (OperationUtils.mCurrentOPerationText) {
                WhiteBoardVariable.Operation.TEXT_NORMAL -> {
                    OperationUtils.mCurrentDrawType = OperationUtils.DRAW_TEXT
                    mFabMenuText.setAddButtonBackground(R.drawable.white_board_text_selected_selector)
                    OperationUtils.mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_CLICK
                }
                WhiteBoardVariable.Operation.TEXT_CLICK -> {
                    val size = OperationUtils.savePoints.size
                    if (size > 0) {
                        val dp = OperationUtils.savePoints[size - 1]
                        if (dp.type == OperationUtils.DRAW_TEXT && dp.drawText!!.status == DrawTextView.TEXT_DETAIL) {
                            changeTextBack()
                            mFabMenuText.expand()
                            OperationUtils.mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_EXPAND
                        }
                    }
                }
                WhiteBoardVariable.Operation.TEXT_EXPAND -> {
                    mFabMenuText.collapse()
                    OperationUtils.mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_CLICK
                }
            }
            WhiteBoardVariable.Operation.PEN_CLICK, WhiteBoardVariable.Operation.ERASER_CLICK -> when (OperationUtils.mCurrentOPerationText) {
                WhiteBoardVariable.Operation.TEXT_NORMAL -> {
                }
                WhiteBoardVariable.Operation.TEXT_CLICK -> {
                    mFabMenuText.clearDraw()
                    mFabMenuText.setAddButtonBackground(R.drawable.white_board_text_selector)
                    OperationUtils.mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_NORMAL
                }
                WhiteBoardVariable.Operation.TEXT_EXPAND -> {
                    mFabMenuText.collapse()
                    mFabMenuText.clearDraw()
                    mFabMenuText.setAddButtonBackground(R.drawable.white_board_text_selector)
                    OperationUtils.mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_NORMAL
                }
            }
            WhiteBoardVariable.Operation.COLOR_CLICK, WhiteBoardVariable.Operation.OUTSIDE_CLICK -> when (OperationUtils.mCurrentOPerationText) {
                WhiteBoardVariable.Operation.TEXT_NORMAL -> {
                }
                WhiteBoardVariable.Operation.TEXT_CLICK -> {
                }
                WhiteBoardVariable.Operation.TEXT_EXPAND -> {
                    mFabMenuText.collapse()
                    OperationUtils.mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_CLICK
                }
            }
        }
    }

    /**
     * 白板工具栏点击切换操作-橡皮擦
     */
    private fun setEraserOperation(currentOperation: Int) {
        when (currentOperation) {
            WhiteBoardVariable.Operation.ERASER_CLICK -> when (OperationUtils.mCurrentOPerationEraser) {
                WhiteBoardVariable.Operation.ERASER_NORMAL -> {
                    OperationUtils.mCurrentDrawType = OperationUtils.DRAW_ERASER
                    mDbView.changeEraser()
                    mFabMenuEraser.setAddButtonBackground(R.drawable.white_board_eraser_selected_selector)
                    OperationUtils.mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_CLICK
                }
                WhiteBoardVariable.Operation.ERASER_CLICK -> {
                    mFabMenuEraser.expand()
                    changeEraserBack()
                    OperationUtils.mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_EXPAND
                }
                WhiteBoardVariable.Operation.ERASER_EXPAND -> {
                    mFabMenuEraser.collapse()
                    OperationUtils.mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_CLICK
                }
            }
            WhiteBoardVariable.Operation.TEXT_CLICK, WhiteBoardVariable.Operation.PEN_CLICK -> when (OperationUtils.mCurrentOPerationEraser) {
                WhiteBoardVariable.Operation.ERASER_NORMAL -> {
                }
                WhiteBoardVariable.Operation.ERASER_CLICK -> {
                    mFabMenuEraser.clearDraw()
                    mFabMenuEraser.setAddButtonBackground(R.drawable.white_board_eraser_selector)
                    OperationUtils.mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_NORMAL
                }
                WhiteBoardVariable.Operation.ERASER_EXPAND -> {
                    mFabMenuEraser.collapse()
                    mFabMenuEraser.clearDraw()
                    mFabMenuEraser.setAddButtonBackground(R.drawable.white_board_eraser_selector)
                    OperationUtils.mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_NORMAL
                }
            }
            WhiteBoardVariable.Operation.COLOR_CLICK, WhiteBoardVariable.Operation.OUTSIDE_CLICK -> when (OperationUtils.mCurrentOPerationEraser) {
                WhiteBoardVariable.Operation.ERASER_NORMAL -> {
                }
                WhiteBoardVariable.Operation.ERASER_CLICK -> {
                }
                WhiteBoardVariable.Operation.ERASER_EXPAND -> {
                    mFabMenuEraser.collapse()
                    OperationUtils.mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_CLICK
                }
            }
        }
    }

    /**
     * 保存当前白板为图片
     */
    fun saveImage() {
        val fileName = StoreUtil.photoSavePath
        val file = File(fileName)
        try {
            val directory = file.parentFile
            if (!directory.exists() && !directory.mkdirs()) {
                showMessage(getString(R.string.white_board_export_fail))
                return
            }
            file.createNewFile()
            val out = FileOutputStream(file)
            mFlView.isDrawingCacheEnabled = true
            mFlView.buildDrawingCache()
            val bitmap = mFlView.drawingCache
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            mFlView.destroyDrawingCache()

            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val uri = Uri.fromFile(file)
            intent.data = uri
            sendBroadcast(intent) //这个广播的目的就是更新图库

            showMessage(getString(R.string.white_board_export_tip) + fileName)
        } catch (e: Exception) {
            showMessage(getString(R.string.white_board_export_fail))
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    @ReceiveEvents(name = Events.WHITE_BOARD_TEXT_EDIT)
    private fun textEdit() { //文字编辑
        mIvWhiteBoardBack.visibility = View.GONE
        mIvWhiteBoardExport.visibility = View.GONE
        mIvWhiteBoardSave.visibility = View.GONE
        mRlBottom.visibility = View.GONE
        mIvWhiteBoardDisable.visibility = View.GONE
        //        mLayoutParams = (RelativeLayout.LayoutParams) mRlContent.getLayoutParams();
//        mLayoutParams.setMargins(OperationUtils.dip2px(24), 0, OperationUtils.dip2px(24), 0);
//        mRlContent.setLayoutParams(mLayoutParams);
        mIvWhiteBoardQuit.visibility = View.VISIBLE
        mIvWhiteBoardConfirm.visibility = View.VISIBLE
    }

    @ReceiveEvents(name = Events.WHITE_BOARD_UNDO_REDO)
    private fun showUndoRedo() { //是否显示撤销、重装按钮
        if (OperationUtils.savePoints.isEmpty()) {
            mIvWhiteBoardUndo.visibility = View.INVISIBLE
            mIvWhiteBoardExport.visibility = View.INVISIBLE
            mIvWhiteBoardSave.visibility = View.INVISIBLE
        } else {
            mIvWhiteBoardUndo.visibility = View.VISIBLE
            mIvWhiteBoardExport.visibility = View.VISIBLE
            mIvWhiteBoardSave.visibility = View.VISIBLE
        }
        if (OperationUtils.deletePoints.isEmpty()) {
            mIvWhiteBoardRedo.visibility = View.INVISIBLE
        } else {
            mIvWhiteBoardRedo.visibility = View.VISIBLE
        }
    }

    override fun onEmojiClick(emojiUnicode: String) {
        if (mEditingEmojiPoint != null) {
            mEditingEmojiPoint!!.emojiUnicode = emojiUnicode
            mEditingEmojiPoint!!.status = DrawEmojiView.EMOJI_VIEW
            mEditingEmojiPoint = null
            mDeView.showPoints()
        } else {
            OperationUtils.mCurrentDrawType = OperationUtils.DRAW_EMOJI
            val drawPoint = OperationUtils.getDrawPointList(OperationUtils.mCurrentIndex)
            val point = DrawPoint()
            point.type = OperationUtils.DRAW_EMOJI
            val drawEmojiPoint = DrawEmojiPoint()
            drawEmojiPoint.id = OperationUtils.newMarkId
            drawEmojiPoint.x = (mFlView.width / 2).toFloat()
            drawEmojiPoint.y = (mFlView.height / 2).toFloat()
            drawEmojiPoint.emojiUnicode = emojiUnicode
            drawEmojiPoint.status = DrawEmojiView.EMOJI_VIEW
            point.drawEmoji = drawEmojiPoint
            drawPoint.savePoints.add(point)
            mDeView.showPoints()
            showUndoRedo()
        }
    }

    override fun onEmojiEdit(drawPoint: DrawPoint) {
        mEditingEmojiPoint = drawPoint.drawEmoji
        val emojiBSFragment = EmojiBSFragment()
        emojiBSFragment.setEmojiListener(this)
        emojiBSFragment.show(supportFragmentManager, emojiBSFragment.tag)
    }
}
