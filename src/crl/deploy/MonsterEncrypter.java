package crl.deploy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import sz.crypt.DESEncrypter;

public class MonsterEncrypter {
	public static void main(String[] args) {
		try (InputStream monsterXmlStream = Files.newInputStream(Paths.get("data/monsters.xml"));
				InputStream monsterCsvStream = Files.newInputStream(Paths.get("data/monsters.csv"))) {
			System.out.println("Writing encrypted file");
			DESEncrypter encrypter = new DESEncrypter("65csvlk3489585f9rjh");
			encrypter.encrypt(monsterXmlStream, Files.newOutputStream(Paths.get("data/monsters.exml")));
			encrypter.encrypt(monsterCsvStream, Files.newOutputStream(Paths.get("data/monsters.ecsv")));
			System.out.println("File written");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
