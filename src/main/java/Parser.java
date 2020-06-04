import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {
    //метод getPage возвращает Document, который считывает весь html документ
    private static Document getPage() throws IOException {
        String url = "http://www.pogoda.spb.ru/";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page; //возвращает page, который сформировался
    }

    //\d символьный знак  --> \d{2}\.\d{2} регулярное выражение
    private static Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}"); //две цифры, точка, две цифры

    private static String getDateFromString(String stringDate) throws Exception {
        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find()) {
            return matcher.group(); //если нашел, группирует найденное
        }
        throw new Exception("Can't extract date from string!"); // если ничего не нашел, сработает исключение

    }


    private static int printPartValues(Elements values, int index) {
        int iterationCount = 4;  //по умолчанию
        int count = 0;
        if (index == 0) { //мы на первом дне
            //смотрим на 3-й valueLine, если содержит "Утро" - значит в сегодняшнем дне у нас есть "день", "вечер", "ночь", т.е 3 итерации
            Element valueLn = values.get(3); //valueLn это вся строка с 6 значениями
            boolean isMorning = valueLn.text().contains("Утро"); //проверяем, содержит ли наш текст "Утро"
            if (isMorning) {
                iterationCount = 3;
            } else if (valueLn.text().contains("День")) {
                iterationCount = 2;
            } else if (valueLn.text().contains("Вечер")) {
                iterationCount = 1;
            }
        }

        for (int i = 0; i < iterationCount; i++) {
            Element valueLine = values.get(index + i);//забирает элементы под индексом 0, 1 и 2

            for (Element td : valueLine.select("td")) { //проходит по td (их 6, это время суток, явления, температура и т.д)
//форматируем вывод
                if (count == 0 || count == 5 || count == 6) {
                    System.out.print(String.format("%-10s", td.text()));
                }
                else if (count == 1) {
                    System.out.print(String.format("%-80s", td.text()));
                }
                else if (count == 2) {
                    System.out.print(String.format("%-31s", td.text()));
                }
                else if (count == 3) {
                    System.out.print(String.format("%-20s", td.text()));
                }
                else if (count == 4) {
                    System.out.print(String.format("%-17s", td.text()));
                }
                    count++;
            }
        count = 0;
        System.out.println();
    }
        return iterationCount;
    }
    private static void tabulation(int count) {

        for (int i = 0; i < count; i++) {
            System.out.print(" ");
        }
    }


    public static void main(String[] arg) throws Exception {
        Document page = getPage(); //это вся страница с погодой
        Element tableWth = page.select("table[class=wt]").first();//вся таблица с погодой wt
        Elements names = tableWth.select("tr[class=wth]"); // названия (их 6) сегодняшнего дня
        Elements values = tableWth.select("tr[valign=top]");  // значения
        int index = 0; //считывает на какой строке мы сейчас находимся
        for (Element name : names) {
            String dateString = name.select("th[id=dt]").text(); // дата, сегодня, день недели
            String date = getDateFromString(dateString); // только дата
            String separator = "_____________________________________________________________________________________________________________________________________________________________________________";
            System.out.println(String.format("%s",separator));
            System.out.printf("          %-78s%-30s%-20s%-23s%-12s%n", "Явления","Температура ","Давление","Влажность","Ветер");
            System.out.println(String.format("%s",separator));
            int iterationCount = printPartValues(values, index);
            index = index + iterationCount; //точка старта(index) увеличится настолько, насколько было напечатано в блоке элемент
        }

    }
}

