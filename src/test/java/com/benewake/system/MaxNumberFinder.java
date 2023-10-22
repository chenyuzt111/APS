public class MaxNumberFinder {

    public static int findMaxNumber(String[] data) {
        int maxNumber = Integer.MIN_VALUE;

        for (String str : data) {
            // 使用正则表达式从字符串中提取数字部分
            String numberPart = str.replaceAll("[^0-9]", "");

            if (!numberPart.isEmpty()) {
                int number = Integer.parseInt(numberPart);
                maxNumber = Math.max(maxNumber, number);
            }
        }

        return maxNumber;
    }

    public static void main(String[] args) {
        String[] data = {"tf-luna-m-4人工艺方案1", "tf-luna-m-2人工艺方案3", "tf-luna-m-10人工艺方案200"};
        int maxNumber = findMaxNumber(data);
        System.out.println("最大的数字是：" + maxNumber);
    }
}
