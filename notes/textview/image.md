```java
  String name = productVoData.getProductName();
        String grade = productVoData.getProductGrade();
        int level = 0;
        if (!TextUtils.isEmpty(grade)) {
            switch (grade) {
                case "LVHSH":
                    level = 3;
                    break;
                case "LVYZD":
                    level = 4;
                    break;
                case "LVZGD":
                    level = 5;
                    break;
            }
        }
        // 确定等级
        int length = name.length();
        StringBuilder stringBuilder = new StringBuilder(name);
        for (int i = 0; i < level; i++) {
            // 此处只是为了替换，字符内容无意义
            stringBuilder.append("1");
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(stringBuilder.toString());
        int start = length;
        for (int i = 0; i < level; i++) {
            int end = start + 1;
            builder.setSpan(new ImageSpan(this, R.drawable.holiday_diamond), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = end;
        }

        titleView.setText(builder);


  ```