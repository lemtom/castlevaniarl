package crl.deploy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import sz.crypt.DESEncrypter;

public class MonsterEncrypter {
	public static void main(String[] args){
		try {
			System.out.println("Writing encrypted file");
			DESEncrypter encrypter = new DESEncrypter("65csvlk3489585f9rjh");
			encrypter.encrypt(Files.newInputStream(Paths.get("data/monsters.xml")), Files.newOutputStream(Paths.get("data/monsters.exml")));
			encrypter.encrypt(Files.newInputStream(Paths.get("data/monsters.csv")), Files.newOutputStream(Paths.get("data/monsters.ecsv")));
			System.out.println("File written");
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
	}}
