package com.veezean.idea.plugin.codereviewer.consts;

import com.veezean.idea.plugin.codereviewer.action.element.CombBoxCreator;
import com.veezean.idea.plugin.codereviewer.action.element.IElementCreator;
import com.veezean.idea.plugin.codereviewer.action.element.TextAreaCreator;
import com.veezean.idea.plugin.codereviewer.action.element.TextFieldCreator;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * 输入类型定义
 *
 * @author Veezean, 公众号 @架构悟道
 * @since 2022/5/22
 */
public enum InputTypeDefine {
    TEXT("TEXT", new TextFieldCreator()),
    TEXT_AREA("TEXTAREA", new TextAreaCreator()),
    COMBO_BOX("COMBO_BOX", new CombBoxCreator());

    private String value;
    private IElementCreator elementCreator;

    InputTypeDefine(String value, IElementCreator elementCreator) {
        this.value = value;
        this.elementCreator = elementCreator;
    }

    public String getValue() {
        return value;
    }

    public IElementCreator getElementCreator() {
        return elementCreator;
    }

    public static IElementCreator getElementCreator(String type) {
        return Arrays.stream(values()).filter(inputTypeDefine -> StringUtils.equals(inputTypeDefine.getValue(), type))
                .findFirst()
                .orElse(TEXT)
                .getElementCreator();
    }
}
