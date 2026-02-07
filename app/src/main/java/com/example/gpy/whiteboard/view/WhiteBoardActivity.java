package com.example.gpy.whiteboard.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gpy.whiteboard.R;
import com.example.gpy.whiteboard.utils.StoreUtil;
import com.example.gpy.whiteboard.view.base.BaseActivity;
import com.example.gpy.whiteboard.view.widget.floatingactionmenu.FloatingActionsMenu;
import com.example.gpy.whiteboard.view.widget.floatingactionmenu.FloatingImageButton;
import com.github.guanpy.library.ann.ReceiveEvents;
import com.github.guanpy.wblib.bean.DrawPoint;
import com.github.guanpy.wblib.utils.Events;
import com.github.guanpy.wblib.utils.OperationUtils;
import com.github.guanpy.wblib.utils.WhiteBoardVariable;
import com.github.guanpy.wblib.widget.DrawPenView;
import com.github.guanpy.wblib.widget.DrawTextLayout;
import com.github.guanpy.wblib.widget.DrawTextView;

import java.io.File;
import java.io.FileOutputStream;



/**
 * 白板工具
 * Created by gpy on 2015/6/2.
 */
public class WhiteBoardActivity extends BaseActivity implements View.OnClickListener {
    ImageView mIvWhiteBoardBook;
    RelativeLayout mRlHead;
    ImageView mIvWhiteBoardBack;
    TextView mTvWhiteBoardHead;
    ImageView mIvWhiteBoardExport;
    ImageView mIvWhiteBoardSave;
    FrameLayout mFlView;
    DrawPenView mDbView;
    DrawTextLayout mDtView;
    FloatingActionsMenu mFabMenuSize;
    FloatingImageButton mBtSizeLarge;
    FloatingImageButton mBtSizeMiddle;
    FloatingImageButton mBtSizeMini;
    FloatingActionsMenu mFabMenuColor;
    FloatingImageButton mBtColorGreen;
    FloatingImageButton mBtColorPurple;
    FloatingImageButton mBtColorPink;
    FloatingImageButton mBtColorOrange;
    FloatingImageButton mBtColorBlack;
    FloatingActionsMenu mFabMenuText;
    FloatingImageButton mBtTextUnderline;
    FloatingImageButton mBtTextItalics;
    FloatingImageButton mBtTextBold;
    FloatingActionsMenu mFabMenuEraser;
    FloatingImageButton mBtEraserLarge;
    FloatingImageButton mBtEraserMiddle;
    FloatingImageButton mBtEraserMini;
    ImageView mIvWhiteBoardUndo;
    ImageView mIvWhiteBoardRedo;
    LinearLayout mLlWhiteBoardPage;
    ImageView mIvWhiteBoardPre;
    TextView mTvWhiteBoardPage;
    ImageView mIvWhiteBoardNext;
    ImageView mIvWhiteBoardAdd;
    ImageView mIvWhiteBoardDisable;
    ImageView mIvWhiteBoardQuit;
    ImageView mIvWhiteBoardConfirm;
    RelativeLayout mRlContent;
    RelativeLayout mRlBottom;
    View mVBottomBack;
    LinearLayout mLlWhiteBoardPre;
    LinearLayout mLlWhiteBoardNext;




    @Override
    protected void afterCreate(Bundle bundle) {
        initView();
        initEvent();

    }
    private void initView() {
        mIvWhiteBoardBook = findViewById(R.id.iv_white_board_book);
        mRlHead = findViewById(R.id.rl_head);
        mIvWhiteBoardBack = findViewById(R.id.iv_white_board_back);
        mTvWhiteBoardHead = findViewById(R.id.tv_white_board_head);
        mIvWhiteBoardExport = findViewById(R.id.iv_white_board_export);
        mIvWhiteBoardSave = findViewById(R.id.iv_white_board_save);
        mFlView = findViewById(R.id.fl_view);
        mDbView = findViewById(R.id.db_view);
        mDtView = findViewById(R.id.dt_view);
        mFabMenuSize = findViewById(R.id.fab_menu_size);
        mBtSizeLarge = findViewById(R.id.bt_size_large);
        mBtSizeMiddle = findViewById(R.id.bt_size_middle);
        mBtSizeMini = findViewById(R.id.bt_size_mini);
        mFabMenuColor = findViewById(R.id.fab_menu_color);
        mBtColorGreen = findViewById(R.id.bt_color_green);
        mBtColorPurple = findViewById(R.id.bt_color_purple);
        mBtColorPink = findViewById(R.id.bt_color_pink);
        mBtColorOrange = findViewById(R.id.bt_color_orange);
        mBtColorBlack = findViewById(R.id.bt_color_black);
        mFabMenuText = findViewById(R.id.fab_menu_text);
        mBtTextUnderline = findViewById(R.id.bt_text_underline);
        mBtTextItalics = findViewById(R.id.bt_text_italics);
        mBtTextBold = findViewById(R.id.bt_text_bold);
        mFabMenuEraser = findViewById(R.id.fab_menu_eraser);
        mBtEraserLarge = findViewById(R.id.bt_eraser_large);
        mBtEraserMiddle = findViewById(R.id.bt_eraser_middle);
        mBtEraserMini = findViewById(R.id.bt_eraser_mini);
        mIvWhiteBoardUndo = findViewById(R.id.iv_white_board_undo);
        mIvWhiteBoardRedo = findViewById(R.id.iv_white_board_redo);
        mLlWhiteBoardPage = findViewById(R.id.ll_white_board_page);
        mIvWhiteBoardPre = findViewById(R.id.iv_white_board_pre);
        mTvWhiteBoardPage = findViewById(R.id.tv_white_board_page);
        mIvWhiteBoardNext = findViewById(R.id.iv_white_board_next);
        mIvWhiteBoardAdd = findViewById(R.id.iv_white_board_add);
        mIvWhiteBoardDisable = findViewById(R.id.iv_white_board_disable);
        mIvWhiteBoardQuit = findViewById(R.id.iv_white_board_quit);
        mIvWhiteBoardConfirm = findViewById(R.id.iv_white_board_confirm);
        mRlContent = findViewById(R.id.rl_content);
        mRlBottom = findViewById(R.id.rl_bottom);
        mVBottomBack = findViewById(R.id.v_bottom_back);
        mLlWhiteBoardPre = findViewById(R.id.ll_white_board_pre);
        mLlWhiteBoardNext = findViewById(R.id.ll_white_board_next);
        changePenBack();
        changeColorBack();
        changeEraserBack();
        ToolsOperation(WhiteBoardVariable.Operation.PEN_NORMAL);
        mDbView.post(new Runnable() {
            @Override
            public void run() {
                showPoints();
            }
        });
    }

    private void initEvent() {
        //头部
        mIvWhiteBoardBack.setOnClickListener(this);
        mIvWhiteBoardExport.setOnClickListener(this);
        mIvWhiteBoardSave.setOnClickListener(this);
        mIvWhiteBoardQuit.setOnClickListener(this);
        mIvWhiteBoardConfirm.setOnClickListener(this);
        mVBottomBack.setOnClickListener(this);
        //画笔尺寸大小
        mFabMenuSize.setOnFloatingActionsMenuClickListener(new FloatingActionsMenu.OnFloatingActionsMenuClickListener() {
            @Override
            public void addButtonLister() {
                ToolsOperation(WhiteBoardVariable.Operation.PEN_CLICK);
            }
        });
        mBtSizeLarge.setOnClickListener(this);
        mBtSizeMiddle.setOnClickListener(this);
        mBtSizeMini.setOnClickListener(this);
        //画笔或者文字颜色
        mFabMenuColor.setOnFloatingActionsMenuClickListener(new FloatingActionsMenu.OnFloatingActionsMenuClickListener() {
            @Override
            public void addButtonLister() {
                ToolsOperation(WhiteBoardVariable.Operation.COLOR_CLICK);
            }
        });
        mBtColorGreen.setOnClickListener(this);
        mBtColorPurple.setOnClickListener(this);
        mBtColorPink.setOnClickListener(this);
        mBtColorOrange.setOnClickListener(this);
        mBtColorBlack.setOnClickListener(this);
        //文字样式
        mFabMenuText.setOnFloatingActionsMenuClickListener(new FloatingActionsMenu.OnFloatingActionsMenuClickListener() {
            @Override
            public void addButtonLister() {
                ToolsOperation(WhiteBoardVariable.Operation.TEXT_CLICK);
            }
        });
        mBtTextUnderline.setOnClickListener(this);
        mBtTextItalics.setOnClickListener(this);
        mBtTextBold.setOnClickListener(this);
        //橡皮擦尺寸大小
        mFabMenuEraser.setOnFloatingActionsMenuClickListener(new FloatingActionsMenu.OnFloatingActionsMenuClickListener() {
            @Override
            public void addButtonLister() {
                ToolsOperation(WhiteBoardVariable.Operation.ERASER_CLICK);
            }
        });
        mBtEraserLarge.setOnClickListener(this);
        mBtEraserMiddle.setOnClickListener(this);
        mBtEraserMini.setOnClickListener(this);

        mIvWhiteBoardUndo.setOnClickListener(this);
        mIvWhiteBoardRedo.setOnClickListener(this);

        mLlWhiteBoardPre.setOnClickListener(this);
        mIvWhiteBoardPre.setOnClickListener(this);
        mLlWhiteBoardNext.setOnClickListener(this);
        mIvWhiteBoardNext.setOnClickListener(this);
        mIvWhiteBoardAdd.setOnClickListener(this);
        mIvWhiteBoardDisable.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_white_board_back) {
            onBackPressed();
        } else if (id == R.id.iv_white_board_quit) {
            afterEdit(false);
        } else if (id == R.id.iv_white_board_confirm) {
            afterEdit(true);
        } else if (id == R.id.iv_white_board_export) {
            ToolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK);
            StoreUtil.saveWhiteBoardPoints();
        } else if (id == R.id.iv_white_board_save) {
            ToolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK);
            saveImage();
        } else if (id == R.id.v_bottom_back) {
            ToolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK);
        } else if (id == R.id.bt_size_large) {
            setPenSize(WhiteBoardVariable.PenSize.LARRGE);
        } else if (id == R.id.bt_size_middle) {
            setPenSize(WhiteBoardVariable.PenSize.MIDDLE);
        } else if (id == R.id.bt_size_mini) {
            setPenSize(WhiteBoardVariable.PenSize.MINI);
        } else if (id == R.id.bt_color_green) {
            setColor(WhiteBoardVariable.Color.GREEN);
        } else if (id == R.id.bt_color_purple) {
            setColor(WhiteBoardVariable.Color.PURPLE);
        } else if (id == R.id.bt_color_pink) {
            setColor(WhiteBoardVariable.Color.PINK);
        } else if (id == R.id.bt_color_orange) {
            setColor(WhiteBoardVariable.Color.ORANGE);
        } else if (id == R.id.bt_color_black) {
            setColor(WhiteBoardVariable.Color.BLACK);
        } else if (id == R.id.bt_text_underline) {
            setTextStyle(WhiteBoardVariable.TextStyle.CHANGE_UNDERLINE);
        } else if (id == R.id.bt_text_italics) {
            setTextStyle(WhiteBoardVariable.TextStyle.CHANGE_ITALICS);
        } else if (id == R.id.bt_text_bold) {
            setTextStyle(WhiteBoardVariable.TextStyle.CHANGE_BOLD);
        } else if (id == R.id.bt_eraser_large) {
            setEraserSize(WhiteBoardVariable.EraserSize.LARRGE);
        } else if (id == R.id.bt_eraser_middle) {
            setEraserSize(WhiteBoardVariable.EraserSize.MIDDLE);
        } else if (id == R.id.bt_eraser_mini) {
             setEraserSize(WhiteBoardVariable.EraserSize.MINI);
        } else if (id == R.id.iv_white_board_undo) {
            ToolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK);
            if (OperationUtils.getInstance().DISABLE) {
                undo();
            }
        } else if (id == R.id.iv_white_board_redo) {
            ToolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK);
            if (OperationUtils.getInstance().DISABLE) {
                redo();
            }
        } else if (id == R.id.ll_white_board_pre || id == R.id.iv_white_board_pre) {
            ToolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK);
            prePage();
        } else if (id == R.id.ll_white_board_next || id == R.id.iv_white_board_next) {
            ToolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK);
            nextPage();
        } else if (id == R.id.iv_white_board_add) {
            ToolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK);
            newPage();
        } else if (id == R.id.iv_white_board_disable) {
            ToolsOperation(WhiteBoardVariable.Operation.OUTSIDE_CLICK);
            if (OperationUtils.getInstance().DISABLE) {
                OperationUtils.getInstance().DISABLE = false;
                mIvWhiteBoardDisable.setImageResource(R.drawable.white_board_undisable_selector);
                mRlBottom.setVisibility(View.GONE);
            } else {
                OperationUtils.getInstance().DISABLE = true;
                mIvWhiteBoardDisable.setImageResource(R.drawable.white_board_disable_selector);
                mRlBottom.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置画笔尺寸
     */
    private void setPenSize(int size) {
        OperationUtils.getInstance().mCurrentPenSize = size;
        changePenBack();
        mDbView.setPenSize();
    }

    /**
     * 切换画笔尺寸按按钮背景
     */
    private void changePenBack() {
        if (OperationUtils.getInstance().mCurrentPenSize == WhiteBoardVariable.PenSize.LARRGE) {
            mBtSizeLarge.drawCircleAndRing(WhiteBoardVariable.PenSize.LARRGE, OperationUtils.getInstance().mCurrentColor);
            mBtSizeMiddle.drawCircle(WhiteBoardVariable.PenSize.MIDDLE);
            mBtSizeMini.drawCircle(WhiteBoardVariable.PenSize.MINI);
        } else if (OperationUtils.getInstance().mCurrentPenSize == WhiteBoardVariable.PenSize.MIDDLE) {
            mBtSizeLarge.drawCircle(WhiteBoardVariable.PenSize.LARRGE);
            mBtSizeMiddle.drawCircleAndRing(WhiteBoardVariable.PenSize.MIDDLE, OperationUtils.getInstance().mCurrentColor);
            mBtSizeMini.drawCircle(WhiteBoardVariable.PenSize.MINI);
        } else if (OperationUtils.getInstance().mCurrentPenSize == WhiteBoardVariable.PenSize.MINI) {
            mBtSizeLarge.drawCircle(WhiteBoardVariable.PenSize.LARRGE);
            mBtSizeMiddle.drawCircle(WhiteBoardVariable.PenSize.MIDDLE);
            mBtSizeMini.drawCircleAndRing(WhiteBoardVariable.PenSize.MINI, OperationUtils.getInstance().mCurrentColor);

        }
    }

    /**
     * 设置颜色
     */
    private void setColor(int color) {
        OperationUtils.getInstance().mCurrentColor = color;
        changeColorBack();
        setPenSize(OperationUtils.getInstance().mCurrentPenSize);
        mDbView.setPenColor();
        mDtView.setTextColor();
    }

    /**
     * 切换颜色控制按钮背景
     */
    private void changeColorBack() {
        if (OperationUtils.getInstance().mCurrentColor == WhiteBoardVariable.Color.BLACK) {
            mFabMenuColor.setAddButtonBackground(R.drawable.white_board_color_black_selector);
        } else if (OperationUtils.getInstance().mCurrentColor == WhiteBoardVariable.Color.ORANGE) {
            mFabMenuColor.setAddButtonBackground(R.drawable.white_board_color_orange_selector);
        } else if (OperationUtils.getInstance().mCurrentColor == WhiteBoardVariable.Color.PINK) {
            mFabMenuColor.setAddButtonBackground(R.drawable.white_board_color_pink_selector);
        } else if (OperationUtils.getInstance().mCurrentColor == WhiteBoardVariable.Color.PURPLE) {
            mFabMenuColor.setAddButtonBackground(R.drawable.white_board_color_purple_selector);
        } else if (OperationUtils.getInstance().mCurrentColor == WhiteBoardVariable.Color.GREEN) {
            mFabMenuColor.setAddButtonBackground(R.drawable.white_board_color_green_selector);
        }
    }

    /**
     * 设置文字风格
     */
    private void setTextStyle(int textStyle) {
        mDtView.setTextStyle(textStyle);
        changeTextBack();
    }

    /**
     * 切换文字相关按钮背景
     */
    private void changeTextBack() {
        int size = OperationUtils.getInstance().getSavePoints().size();
        if (size < 1) {
            return;
        }
        DrawPoint dp = OperationUtils.getInstance().getSavePoints().get(size - 1);
        if (dp.getType() != OperationUtils.DRAW_TEXT) {
            return;
        }
        if (dp.getDrawText().getIsUnderline()) {
            mBtTextUnderline.setBackgroundResource(R.drawable.white_board_text_underline_selected_selector);
        } else {
            mBtTextUnderline.setBackgroundResource(R.drawable.white_board_text_underline_selector);
        }

        if (dp.getDrawText().getIsItalics()) {
            mBtTextItalics.setBackgroundResource(R.drawable.white_board_text_italics_selected_selector);
        } else {
            mBtTextItalics.setBackgroundResource(R.drawable.white_board_text_italics_selector);
        }

        if (dp.getDrawText().getIsBold()) {
            mBtTextBold.setBackgroundResource(R.drawable.white_board_text_bold_selected_selector);
        } else {
            mBtTextBold.setBackgroundResource(R.drawable.white_board_text_bold_selector);
        }
    }

    /**
     * 设置橡皮擦尺寸
     */
    private void setEraserSize(int size) {
        OperationUtils.getInstance().mCurrentEraserSize = size;
        changeEraserBack();
        mDbView.setEraserSize();

    }

    /**
     * 切换橡皮擦尺寸按钮背景
     */
    private void changeEraserBack() {
        if (OperationUtils.getInstance().mCurrentEraserSize == WhiteBoardVariable.EraserSize.LARRGE) {
            mBtEraserLarge.drawCircleAndRing(WhiteBoardVariable.EraserSize.LARRGE, WhiteBoardVariable.Color.BLACK);
            mBtEraserMiddle.drawCircle(WhiteBoardVariable.EraserSize.MIDDLE, WhiteBoardVariable.Color.BLACK);
            mBtEraserMini.drawCircle(WhiteBoardVariable.EraserSize.MINI, WhiteBoardVariable.Color.BLACK);
        } else if (OperationUtils.getInstance().mCurrentEraserSize == WhiteBoardVariable.EraserSize.MIDDLE) {
            mBtEraserLarge.drawCircle(WhiteBoardVariable.EraserSize.LARRGE, WhiteBoardVariable.Color.BLACK);
            mBtEraserMiddle.drawCircleAndRing(WhiteBoardVariable.EraserSize.MIDDLE, WhiteBoardVariable.Color.BLACK);
            mBtEraserMini.drawCircle(WhiteBoardVariable.EraserSize.MINI, WhiteBoardVariable.Color.BLACK);
        } else if (OperationUtils.getInstance().mCurrentEraserSize == WhiteBoardVariable.EraserSize.MINI) {
            mBtEraserLarge.drawCircle(WhiteBoardVariable.EraserSize.LARRGE, WhiteBoardVariable.Color.BLACK);
            mBtEraserMiddle.drawCircle(WhiteBoardVariable.EraserSize.MIDDLE, WhiteBoardVariable.Color.BLACK);
            mBtEraserMini.drawCircleAndRing(WhiteBoardVariable.EraserSize.MINI, WhiteBoardVariable.Color.BLACK);

        }
    }

    /**
     * 新建白板
     */
    private void newPage() {
        OperationUtils.getInstance().newPage();
        showPoints();
    }

    /**
     * 上一页
     */
    private void prePage() {
        if (OperationUtils.getInstance().mCurrentIndex > 0) {
            OperationUtils.getInstance().mCurrentIndex--;
            showPoints();
        }
    }

    /**
     * 下一页
     */
    private void nextPage() {
        if (OperationUtils.getInstance().mCurrentIndex + 1 < OperationUtils.getInstance().getDrawPointSize()) {
            OperationUtils.getInstance().mCurrentIndex++;
            showPoints();
        }
    }

    /**
     * 重新显示白板
     */
    private void showPoints() {
        mDbView.showPoints();
        mDtView.showPoints();
        mTvWhiteBoardPage.setText("" + (OperationUtils.getInstance().mCurrentIndex + 1) + "/" + OperationUtils.getInstance().getDrawPointSize());
        showPage();
        showUndoRedo();
    }

    /**
     * 显示上下页是否可点击
     */
    private void showPage() {
        if (OperationUtils.getInstance().mCurrentIndex + 1 == OperationUtils.getInstance().getDrawPointSize()) {
            mIvWhiteBoardNext.setImageResource(R.drawable.white_board_next_page_click);
        } else {
            mIvWhiteBoardNext.setImageResource(R.drawable.white_board_next_page_selector);
        }
        if (OperationUtils.getInstance().mCurrentIndex == 0) {
            mIvWhiteBoardPre.setImageResource(R.drawable.white_board_pre_page_click);
        } else {
            mIvWhiteBoardPre.setImageResource(R.drawable.white_board_pre_page_selector);
        }

    }

    /**
     * 撤销
     */
    private void undo() {
        int size = OperationUtils.getInstance().getSavePoints().size();
        if (size == 0) {
            return;
        } else {
            OperationUtils.getInstance().getDeletePoints().add(OperationUtils.getInstance().getSavePoints().get(size - 1));
            OperationUtils.getInstance().getSavePoints().remove(size - 1);
            size = OperationUtils.getInstance().getDeletePoints().size();
            if (OperationUtils.getInstance().getDeletePoints().get(size - 1).getType() == OperationUtils.DRAW_PEN) {
                mDbView.undo();
            } else if (OperationUtils.getInstance().getDeletePoints().get(size - 1).getType() == OperationUtils.DRAW_TEXT) {
                mDtView.undo();
            }
            showUndoRedo();
        }

    }

    /**
     * 重做
     */
    private void redo() {
        int size = OperationUtils.getInstance().getDeletePoints().size();
        if (size == 0) {
            return;
        } else {
            OperationUtils.getInstance().getSavePoints().add(OperationUtils.getInstance().getDeletePoints().get(size - 1));
            OperationUtils.getInstance().getDeletePoints().remove(size - 1);
            size = OperationUtils.getInstance().getSavePoints().size();
            if (OperationUtils.getInstance().getSavePoints().get(size - 1).getType() == OperationUtils.DRAW_PEN) {
                mDbView.redo();
            } else if (OperationUtils.getInstance().getSavePoints().get(size - 1).getType() == OperationUtils.DRAW_TEXT) {
                mDtView.redo();
            }
            showUndoRedo();
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_white_board;
    }

    /**
     * 文字编辑之后
     */
    private void afterEdit(boolean isSave) {
        mIvWhiteBoardBack.setVisibility(View.VISIBLE);
        mIvWhiteBoardExport.setVisibility(View.VISIBLE);
        mIvWhiteBoardSave.setVisibility(View.VISIBLE);
        mRlBottom.setVisibility(View.VISIBLE);
        mIvWhiteBoardDisable.setVisibility(View.VISIBLE);
//        mLayoutParams = (RelativeLayout.LayoutParams) mRlContent.getLayoutParams();
//        mLayoutParams.setMargins(OperationUtils.dip2px(24), 0, OperationUtils.dip2px(24), OperationUtils.dip2px(24));
//        mRlContent.setLayoutParams(mLayoutParams);
        mIvWhiteBoardQuit.setVisibility(View.GONE);
        mIvWhiteBoardConfirm.setVisibility(View.GONE);
        mDbView.showPoints();
        mDtView.afterEdit(isSave);
    }

    /**
     * 白板工具栏点击切换操作
     */
    private void ToolsOperation(int currentOperation) {
        setPenOperation(currentOperation);
        setColorOperation(currentOperation);
        setTextOperation(currentOperation);
        setEraserOperation(currentOperation);
        showOutSideView();
    }

    /**
     * 显示挡板
     */
    private void showOutSideView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (OperationUtils.getInstance().mCurrentOPerationPen == WhiteBoardVariable.Operation.PEN_EXPAND
                        || OperationUtils.getInstance().mCurrentOPerationColor == WhiteBoardVariable.Operation.COLOR_EXPAND
                        || OperationUtils.getInstance().mCurrentOPerationText == WhiteBoardVariable.Operation.TEXT_EXPAND
                        || OperationUtils.getInstance().mCurrentOPerationEraser == WhiteBoardVariable.Operation.ERASER_EXPAND) {
                    mVBottomBack.setVisibility(View.VISIBLE);
                } else {
                    mVBottomBack.setVisibility(View.GONE);
                }
            }
        }, 100);

    }

    /**
     * 白板工具栏点击切换操作-画笔
     */
    private void setPenOperation(int currentOperation) {
        switch (currentOperation) {
            case WhiteBoardVariable.Operation.PEN_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationPen) {
                    case WhiteBoardVariable.Operation.PEN_NORMAL:
                        OperationUtils.getInstance().mCurrentDrawType = OperationUtils.DRAW_PEN;
                        mDbView.setPaint(null);
                        mFabMenuSize.setAddButtonBackground(R.drawable.white_board_pen_selected_selector);
                        OperationUtils.getInstance().mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_CLICK;
                        break;
                    case WhiteBoardVariable.Operation.PEN_CLICK:
                        mFabMenuSize.expand();
                        changePenBack();
                        OperationUtils.getInstance().mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_EXPAND;
                        break;
                    case WhiteBoardVariable.Operation.PEN_EXPAND:
                        mFabMenuSize.collapse();
                        OperationUtils.getInstance().mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_CLICK;
                        break;
                }
                break;
            case WhiteBoardVariable.Operation.TEXT_CLICK:
            case WhiteBoardVariable.Operation.ERASER_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationPen) {
                    case WhiteBoardVariable.Operation.PEN_NORMAL:
                        break;
                    case WhiteBoardVariable.Operation.PEN_CLICK:
                        mFabMenuSize.clearDraw();
                        mFabMenuSize.setAddButtonBackground(R.drawable.white_board_pen_selector);
                        OperationUtils.getInstance().mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_NORMAL;
                        break;
                    case WhiteBoardVariable.Operation.PEN_EXPAND:
                        mFabMenuSize.collapse();
                        mFabMenuSize.clearDraw();
                        mFabMenuSize.setAddButtonBackground(R.drawable.white_board_pen_selector);
                        OperationUtils.getInstance().mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_NORMAL;
                        break;
                }
                break;
            case WhiteBoardVariable.Operation.COLOR_CLICK:
            case WhiteBoardVariable.Operation.OUTSIDE_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationPen) {
                    case WhiteBoardVariable.Operation.PEN_NORMAL:
                        break;
                    case WhiteBoardVariable.Operation.PEN_CLICK:
                        break;
                    case WhiteBoardVariable.Operation.PEN_EXPAND:
                        mFabMenuSize.collapse();
                        OperationUtils.getInstance().mCurrentOPerationPen = WhiteBoardVariable.Operation.PEN_CLICK;
                        break;
                }
                break;

        }

    }

    /**
     * 白板工具栏点击切换操作-颜色
     */
    private void setColorOperation(int currentOperation) {
        switch (currentOperation) {
            case WhiteBoardVariable.Operation.PEN_CLICK:
            case WhiteBoardVariable.Operation.TEXT_CLICK:
            case WhiteBoardVariable.Operation.ERASER_CLICK:
            case WhiteBoardVariable.Operation.OUTSIDE_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationColor) {
                    case WhiteBoardVariable.Operation.COLOR_NORMAL:
                        break;
                    case WhiteBoardVariable.Operation.COLOR_EXPAND:
                        mFabMenuColor.collapse();
                        OperationUtils.getInstance().mCurrentOPerationColor = WhiteBoardVariable.Operation.COLOR_NORMAL;
                        break;
                }
                break;
            case WhiteBoardVariable.Operation.COLOR_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationColor) {
                    case WhiteBoardVariable.Operation.COLOR_NORMAL:
                        mFabMenuColor.expand();
                        OperationUtils.getInstance().mCurrentOPerationColor = WhiteBoardVariable.Operation.COLOR_EXPAND;
                        break;
                    case WhiteBoardVariable.Operation.COLOR_EXPAND:
                        mFabMenuColor.collapse();
                        OperationUtils.getInstance().mCurrentOPerationColor = WhiteBoardVariable.Operation.COLOR_NORMAL;
                        break;
                }
                break;

        }

    }

    /**
     * 白板工具栏点击切换操作-文字
     */
    private void setTextOperation(int currentOperation) {
        switch (currentOperation) {
            case WhiteBoardVariable.Operation.TEXT_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationText) {
                    case WhiteBoardVariable.Operation.TEXT_NORMAL:
                        OperationUtils.getInstance().mCurrentDrawType = OperationUtils.DRAW_TEXT;
                        mFabMenuText.setAddButtonBackground(R.drawable.white_board_text_selected_selector);
                        OperationUtils.getInstance().mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_CLICK;
                        break;
                    case WhiteBoardVariable.Operation.TEXT_CLICK:
                        int size = OperationUtils.getInstance().getSavePoints().size();
                        if (size > 0) {
                            DrawPoint dp = OperationUtils.getInstance().getSavePoints().get(size - 1);
                            if (dp.getType() == OperationUtils.DRAW_TEXT && dp.getDrawText().getStatus() == DrawTextView.TEXT_DETAIL) {
                                changeTextBack();
                                mFabMenuText.expand();
                                OperationUtils.getInstance().mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_EXPAND;
                            }
                        }
                        break;
                    case WhiteBoardVariable.Operation.TEXT_EXPAND:
                        mFabMenuText.collapse();
                        OperationUtils.getInstance().mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_CLICK;
                        break;
                }
                break;
            case WhiteBoardVariable.Operation.PEN_CLICK:
            case WhiteBoardVariable.Operation.ERASER_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationText) {
                    case WhiteBoardVariable.Operation.TEXT_NORMAL:
                        break;
                    case WhiteBoardVariable.Operation.TEXT_CLICK:
                        mFabMenuText.clearDraw();
                        mFabMenuText.setAddButtonBackground(R.drawable.white_board_text_selector);
                        OperationUtils.getInstance().mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_NORMAL;
                        break;
                    case WhiteBoardVariable.Operation.TEXT_EXPAND:
                        mFabMenuText.collapse();
                        mFabMenuText.clearDraw();
                        mFabMenuText.setAddButtonBackground(R.drawable.white_board_text_selector);
                        OperationUtils.getInstance().mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_NORMAL;
                        break;
                }
                break;
            case WhiteBoardVariable.Operation.COLOR_CLICK:
            case WhiteBoardVariable.Operation.OUTSIDE_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationText) {
                    case WhiteBoardVariable.Operation.TEXT_NORMAL:
                        break;
                    case WhiteBoardVariable.Operation.TEXT_CLICK:
                        break;
                    case WhiteBoardVariable.Operation.TEXT_EXPAND:
                        mFabMenuText.collapse();
                        OperationUtils.getInstance().mCurrentOPerationText = WhiteBoardVariable.Operation.TEXT_CLICK;
                        break;
                }
                break;

        }

    }

    /**
     * 白板工具栏点击切换操作-橡皮擦
     */
    private void setEraserOperation(int currentOperation) {
        switch (currentOperation) {
            case WhiteBoardVariable.Operation.ERASER_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationEraser) {
                    case WhiteBoardVariable.Operation.ERASER_NORMAL:
                        OperationUtils.getInstance().mCurrentDrawType = OperationUtils.DRAW_ERASER;
                        mDbView.changeEraser();
                        mFabMenuEraser.setAddButtonBackground(R.drawable.white_board_eraser_selected_selector);
                        OperationUtils.getInstance().mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_CLICK;
                        break;
                    case WhiteBoardVariable.Operation.ERASER_CLICK:
                        mFabMenuEraser.expand();
                        changeEraserBack();
                        OperationUtils.getInstance().mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_EXPAND;
                        break;
                    case WhiteBoardVariable.Operation.ERASER_EXPAND:
                        mFabMenuEraser.collapse();
                        OperationUtils.getInstance().mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_CLICK;
                        break;
                }
                break;
            case WhiteBoardVariable.Operation.TEXT_CLICK:
            case WhiteBoardVariable.Operation.PEN_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationEraser) {
                    case WhiteBoardVariable.Operation.ERASER_NORMAL:
                        break;
                    case WhiteBoardVariable.Operation.ERASER_CLICK:
                        mFabMenuEraser.clearDraw();
                        mFabMenuEraser.setAddButtonBackground(R.drawable.white_board_eraser_selector);
                        OperationUtils.getInstance().mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_NORMAL;
                        break;
                    case WhiteBoardVariable.Operation.ERASER_EXPAND:
                        mFabMenuEraser.collapse();
                        mFabMenuEraser.clearDraw();
                        mFabMenuEraser.setAddButtonBackground(R.drawable.white_board_eraser_selector);
                        OperationUtils.getInstance().mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_NORMAL;
                        break;
                }
                break;
            case WhiteBoardVariable.Operation.COLOR_CLICK:
            case WhiteBoardVariable.Operation.OUTSIDE_CLICK:
                switch (OperationUtils.getInstance().mCurrentOPerationEraser) {
                    case WhiteBoardVariable.Operation.ERASER_NORMAL:
                        break;
                    case WhiteBoardVariable.Operation.ERASER_CLICK:
                        break;
                    case WhiteBoardVariable.Operation.ERASER_EXPAND:
                        mFabMenuEraser.collapse();
                        OperationUtils.getInstance().mCurrentOPerationEraser = WhiteBoardVariable.Operation.ERASER_CLICK;
                        break;
                }
                break;

        }

    }

    /**
     * 保存当前白板为图片
     */
    public void saveImage() {
        String fileName = StoreUtil.getPhotoSavePath();
        Log.e("gpy", fileName);
        File file = new File(fileName);
        try {
            File directory = file.getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                showMessage(getString(R.string.white_board_export_fail));
                return;
            }
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            mFlView.setDrawingCacheEnabled(true);
            mFlView.buildDrawingCache();
            Bitmap bitmap = mFlView.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            mFlView.destroyDrawingCache();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            sendBroadcast(intent);//这个广播的目的就是更新图库

            showMessage(getString(R.string.white_board_export_tip) + fileName);
        } catch (Exception e) {
            showMessage(getString(R.string.white_board_export_fail));
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @ReceiveEvents(name = Events.WHITE_BOARD_TEXT_EDIT)
    private void textEdit() {//文字编辑
        mIvWhiteBoardBack.setVisibility(View.GONE);
        mIvWhiteBoardExport.setVisibility(View.GONE);
        mIvWhiteBoardSave.setVisibility(View.GONE);
        mRlBottom.setVisibility(View.GONE);
        mIvWhiteBoardDisable.setVisibility(View.GONE);
//        mLayoutParams = (RelativeLayout.LayoutParams) mRlContent.getLayoutParams();
//        mLayoutParams.setMargins(OperationUtils.dip2px(24), 0, OperationUtils.dip2px(24), 0);
//        mRlContent.setLayoutParams(mLayoutParams);

        mIvWhiteBoardQuit.setVisibility(View.VISIBLE);
        mIvWhiteBoardConfirm.setVisibility(View.VISIBLE);
    }

    @ReceiveEvents(name = Events.WHITE_BOARD_UNDO_REDO)
    private void showUndoRedo() {//是否显示撤销、重装按钮
        if (OperationUtils.getInstance().getSavePoints().isEmpty()) {
            mIvWhiteBoardUndo.setVisibility(View.INVISIBLE);
            mIvWhiteBoardExport.setVisibility(View.INVISIBLE);
            mIvWhiteBoardSave.setVisibility(View.INVISIBLE);
        } else {
            mIvWhiteBoardUndo.setVisibility(View.VISIBLE);
            mIvWhiteBoardExport.setVisibility(View.VISIBLE);
            mIvWhiteBoardSave.setVisibility(View.VISIBLE);
        }
        if (OperationUtils.getInstance().getDeletePoints().isEmpty()) {
            mIvWhiteBoardRedo.setVisibility(View.INVISIBLE);
        } else {
            mIvWhiteBoardRedo.setVisibility(View.VISIBLE);
        }
    }

}
