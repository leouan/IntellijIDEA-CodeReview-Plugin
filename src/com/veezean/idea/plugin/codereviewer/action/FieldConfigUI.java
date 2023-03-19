package com.veezean.idea.plugin.codereviewer.action;

import cn.hutool.json.JSONUtil;
import com.veezean.idea.plugin.codereviewer.common.GlobalConfigManager;
import com.veezean.idea.plugin.codereviewer.common.NetworkOperationHelper;
import com.veezean.idea.plugin.codereviewer.model.RecordColumns;
import com.veezean.idea.plugin.codereviewer.util.CommonUtil;
import com.veezean.idea.plugin.codereviewer.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 字段配置界面
 *
 * @author Veezean, 公众号 @架构悟道
 * @date 2023/3/12
 */
public class FieldConfigUI extends JDialog {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea fieldConfigTextArea;
    private JLabel restoreToDefault;
    private JLabel showHelpBtn;

    public FieldConfigUI(JComponent ideMainWindow) {

        setLocation(CommonUtil.getWindowRelativePoint(ideMainWindow, WIDTH, HEIGHT));
        setSize(WIDTH, HEIGHT);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // 加载当前配置信息
        RecordColumns recordColumns = GlobalConfigManager.getInstance().getCustomConfigColumns();
        fieldConfigTextArea.setText(JSONUtil.toJsonPrettyStr(recordColumns));

        restoreToDefault.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int resp = JOptionPane.showConfirmDialog(contentPane, "确定要恢复成系统默认配置吗？此操作不可恢复!",
                        "重置确认",
                        JOptionPane.YES_NO_OPTION);
                if (resp != 0) {
                    Logger.info("取消重置配置操作...");
                    return;
                }

                RecordColumns systemDefaultColumns = GlobalConfigManager.getInstance().getSystemDefaultColumns();
                GlobalConfigManager.getInstance().saveCustomConfigColumn(systemDefaultColumns);
                fieldConfigTextArea.setText(JSONUtil.toJsonPrettyStr(systemDefaultColumns));
            }
        });
        restoreToDefault.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        showHelpBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                NetworkOperationHelper.openBrowser("http://blog.codingcoder.cn/post/codereviewfieldmodifyhelper.html");
            }
        });
        showHelpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void onOK() {
        // 更新自定义字段
        RecordColumns recordColumns = JSONUtil.toBean(fieldConfigTextArea.getText(), RecordColumns.class);
        GlobalConfigManager.getInstance().saveCustomConfigColumn(recordColumns);

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void showConfigUI(JComponent rootPanel) {
        FieldConfigUI dialog = new FieldConfigUI(rootPanel);
        dialog.pack();
        dialog.setVisible(true);
    }

}
