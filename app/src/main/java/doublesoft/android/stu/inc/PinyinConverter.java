package doublesoft.android.stu.inc;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class PinyinConverter {

    private String targetName;
    private String pinyinName;


    /**
     * @param targetName 需要被转成拼音的字段名
     * @param pinyinName 新建用于保存拼音字段的字段名
     */
    public PinyinConverter(@NonNull String targetName, @NonNull String pinyinName) {
        this.targetName = targetName;
        this.pinyinName = pinyinName;
    }

    public static String firstChar(String text) {
        if (!TextUtils.isEmpty(text)) {
            return text.toUpperCase().substring(0, 1);
        }
        return "";
    }

    /**
     * 给定汉字，输出拼音
     */
    public static String getPinYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();// 把字符串转化成字符数组
        StringBuilder output = new StringBuilder();

        try {
            for (char anInput : input) {
                // \\u4E00是unicode编码，判断是不是中文
                if (Character.toString(anInput).matches(
                        "[\\u4E00-\\u9FA5]+")) {
                    // 将汉语拼音的全拼存到temp数组
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                            anInput, format);
                    // 取拼音的第一个读音
                    output.append(temp[0]);
                }
                // 大写字母转化成小写字母
                else if (anInput > 'A' && anInput < 'Z') {
                    output.append(Character.toString(anInput));
                    output = new StringBuilder(output.toString().toLowerCase());
                }
                output.append(Character.toString(anInput));
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return output.toString();
    }

    public static String getPinYinFirstChar(String inputString) {
        return firstChar(getPinYin(inputString));
    }

    public List<List<ContentValues>> getListOrderAndGroupByPinyin(List<ContentValues> inputList) {
        initPinyin(inputList);
        inputList = copyListWithPinyinSort(inputList);
        return getGroupByPinyin(inputList);
    }

    /**
     * 为给定的UserName字段添加拼音字段
     */
    public void initPinyin(Collection<ContentValues> valuesList) {
        if (valuesList == null) {
            return;
        }
        for (ContentValues values : valuesList) {
            if (values.containsKey(targetName) && !values.containsKey(pinyinName)) {
                String pinyin = getPinYin(values.getAsString(targetName));
                values.put(pinyinName, pinyin);
            }
        }
    }

    /**
     * 将ContentValues按照其中pinyin字段的拼音首字母进行归类，但并不排序
     */
    private List<List<ContentValues>> getGroupByPinyin(List<ContentValues> list) {
        List<List<ContentValues>> resultList = new ArrayList<>();
        Map<String, List<ContentValues>> map = new LinkedHashMap<>();
        if (list != null) {
            for (ContentValues values : list) {
                String firstChar = firstChar(values.getAsString(pinyinName));
                if (map.containsKey(firstChar)) {
                    map.get(firstChar).add(values);
                } else {
                    List<ContentValues> arr = new ArrayList<>();
                    arr.add(values);
                    map.put(firstChar, arr);
                }

            }
            for (Map.Entry<String, List<ContentValues>> entry : map.entrySet()) {
                resultList.add(entry.getValue());
            }
        }
        return resultList;
    }

    /**
     * copy一份新的list并排序，排序不影响原先的list
     */
    public List<ContentValues> copyListWithPinyinSort(List<ContentValues> list) {
        list = new ArrayList<>(list);
        initPinyin(list);
        Collections.sort(list, new PinyinComparator());
        return list;

    }

    /**
     * 通过拼音排序的比较器
     */
    private class PinyinComparator implements Comparator<ContentValues> {
        public int compare(ContentValues cv1, ContentValues cv2) {
            String str1 = getPinYin(cv1.getAsString(pinyinName));
            String str2 = getPinYin(cv2.getAsString(pinyinName));
            return str1.compareTo(str2);
        }
    }
}
