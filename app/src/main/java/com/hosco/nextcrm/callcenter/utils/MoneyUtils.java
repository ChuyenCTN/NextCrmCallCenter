package com.hosco.nextcrm.callcenter.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MoneyUtils {

    public static String formatMoney(double amount) {
        String price = String.format("%.2f", amount);
        price = price.replace(",", ".");
        if (price.contains(".")) {
            if (price.substring(price.indexOf('.') + 1).equalsIgnoreCase("00")) {
                price = price.substring(0, price.indexOf('.'));
            }
        }
        return formatNumber((int) amount) + " Vpoint";
    }

    public static String formatNumber(int number) {
        try {
            if (number < 0 && number > -1000 || number > 0 && number < 1000) {
                return String.valueOf(number);
            }
            NumberFormat formatter = new DecimalFormat("###,###");
            String resp = formatter.format(number);
            resp = resp.replaceAll(",", ".");
            return resp;
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatString(String number1, int type) {
//        int number = Integer.parseInt(number1);

        try {
            Double number = Double.parseDouble(number1);
            if (number < 0 && number > -1000 || number > 0 && number < 1000) {
                return String.valueOf(number);
            }
            NumberFormat formatter = new DecimalFormat("###,###");
            String resp = formatter.format(number);
            resp = resp.replaceAll(",", ".");
            if (type == 0)
                return resp;
            else
                return resp;
        } catch (Exception e) {
            if (type == 0)
                return "0";
            else
                return "0";
        }
    }

    public static String formatString(String number1) {
//        int number = Integer.parseInt(number1);
        try {
//            Log.e("formatString", " " + number1 + " " + number1.length());
            if (number1.length() >= 4) {
                double number = Double.parseDouble(number1);
                if (number < 0 && number > -1000 || number > 0 && number < 1000) {
                    return String.valueOf(number);
                }
                NumberFormat formatter = new DecimalFormat("###,###");
                String resp = formatter.format(number);
                resp = resp.replaceAll(",", ".");
                return resp;
            } else return number1;
        } catch (Exception e) {
            return "";
        }

    }

    public static String formatVNDToVpoint(String number1) {
        try {
            Double number = Double.parseDouble(number1);
            if (number < 0 && number > -1000 || number > 0 && number < 1000) {
                return String.valueOf(number);
            }
            NumberFormat formatter = new DecimalFormat("###,###");
            String resp = formatter.format(number);
            resp = resp.replaceAll(",", ".");
            return resp + " Vpoint";
        } catch (Exception e) {
            return "0 Vpoint";
        }
    }
}
