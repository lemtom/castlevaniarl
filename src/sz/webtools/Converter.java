package sz.webtools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import sz.util.FileUtil;

public class Converter {
	public static void main (String[] args){
		File[] files = new File(args[0]).listFiles();

        for (File file : files) {
            try {
                if (file.isDirectory())
                    continue;
                BufferedReader r = FileUtil.getReader(file.getAbsolutePath());
                BufferedWriter w = FileUtil.getWriter(file.getParent() + "/html/" + file.getName() + ".html");
                w.write("<table width = \"80%\">");
                w.newLine();
                w.write("<tr><th width = \"70%\">Game</th><th width = \"30%\">Author</th></tr>");
                w.newLine();
                String line = r.readLine();
                while (line != null) {
                    System.out.println(line);
                    String[] tokens = line.split(",");
                    w.write("<tr>");
                    w.newLine();
                    if (tokens[1].trim().equals("LOST")) {
                        w.write("<td nowrap>" + tokens[0].trim() + "</td>");
                        w.newLine();
                    } else {
                        w.write("<td nowrap><a href=\"" + tokens[1].trim() + "\">" + tokens[0].trim() + "</a></td>");
                        w.newLine();
                    }
                    w.write("<td nowrap>" + tokens[2].trim() + "</td>");
                    w.newLine();
                    w.write("</tr>");
                    w.newLine();
                    line = r.readLine();
                }
                w.write("</table>");
                w.newLine();
                w.close();
                r.close();
            } catch (IOException ieo) {
                ieo.printStackTrace();
            }

        }
	}
}
