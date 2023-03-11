package com.veezean.idea.plugin.codereviewer.util;

import cn.hutool.crypto.digest.MD5;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.veezean.idea.plugin.codereviewer.action.ManageReviewCommentUI;
import com.veezean.idea.plugin.codereviewer.common.InnerProjectCache;
import com.veezean.idea.plugin.codereviewer.common.ProjectInstanceManager;
import com.veezean.idea.plugin.codereviewer.util.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * 通用工具类
 *
 * @author Wang Weiren
 * @since 2019/10/2
 */
public class CommonUtil {

    private static final ThreadLocal<SimpleDateFormat> SDF = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    private static final String ICON_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABdUlEQVQ4T52TQUsCYRCG33XVXHODOkkRSElBoIcQ21t4CLwkFN26R3+gS4foF9S1P6DXYL14E4MoTfIgBJUQCAkd8pCYputuzJghurtkA8Me9pv3e2bm/QTjWjmFA+sAtjFZpKHjXjBulFsAG5PV/p7Ok4BhVdxoCZAly99cZitwkfEhEuxwWsWYQDLn5YLVBQ3Fihvzcz1OtSDxd1TMlODx1QkS2t/8RK0uIluewmG8yQKjYSpA6Et+DV0NUFY6qL6LeKi6WNBW4CQ1g0S0zehri10UKy4+Hw50Uas7ITr6Ax0WMiWgfmd9Ou6e3QgHOlj2ayi9uLGrtP7WAs3gMi/hYKuJng6cqTKO9z5MVzpGQLfLko5Y6AuDYUaC1IKIWKjN2xmOMYGBeWjyqSsvfB4DRzsN3oCZsSyNRINUCx4moRwEERJJtuxBItqydyK1QFYe3j9RJHPTTENkRKBO8hKJ4KnWN1rp/C0t/Dzn+D9eZB46Mt+kt7lb5jbI3QAAAABJRU5ErkJggg==";

    public static String md5(String original) {
        return MD5.create().digestHex(original, StandardCharsets.UTF_8);
    }
    
    public static String time2String(long millis) {
        return SDF.get().format(new Date(millis));
    }

    /**
     * 格式化时间信息，作为文件名称中使用
     *
     * @return 格式化后的时间字符串
     */
    public static String getFormattedTimeForFileName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(new Date());
    }

    public static ImageIcon getDefaultIcon() {
        try {
            Image image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(ICON_BASE64)));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ImageIcon();
    }

    /**
     * 静默关流处理方法
     *
     * @param closeable 可关闭流
     */
    public static void closeQuitely(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新加载指定项目的评审信息
     *
     * @param project 待处理的项目
     */
    public synchronized static void reloadCommentListShow(Project project) {
        try {
            InnerProjectCache projectCache = ProjectInstanceManager.getInstance().getProjectCache(project.getLocationHash());

            ManageReviewCommentUI manageReviewCommentUI = projectCache.getManageReviewCommentUI();
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("CodeReview");

            if (manageReviewCommentUI != null && toolWindow != null) {
                manageReviewCommentUI.refreshTableDataShow();
            } else {
                Logger.info("重新加载指定项目的评审信息");
            }
        } catch (Exception ex) {
            Logger.error("重新加载指定项目的评审信息失败", ex);
        }
    }

    public static String getFileFullName(PsiFile psiFile) {
        String classPath = psiFile.getVirtualFile().getName();
        if (psiFile instanceof PsiJavaFile) {
            // 如果是java文件，则一并存储下packagename，解决
            classPath = ((PsiJavaFile)psiFile).getPackageName() + "|" + classPath;
        }
        return classPath;
    }
}
