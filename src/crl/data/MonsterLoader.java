package crl.data;

import crl.game.CRLException;
import crl.monster.*;
import crl.ui.AppearanceFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.ParserAdapter;

import sz.crypt.*;
import uk.co.wilson.xml.MinML;

public class MonsterLoader {
	public static MonsterDefinition[] getBaseMonsters(String monsterFile) throws CRLException {
		BufferedReader br = null;
		try {
			ArrayList<MonsterDefinition> vecMonsters = new ArrayList<>(10);
			DESEncrypter encrypter = new DESEncrypter("65csvlk3489585f9rjh");
			br = new BufferedReader(
					new InputStreamReader(encrypter.decrypt(Files.newInputStream(Paths.get(monsterFile)))));
			String line = br.readLine();
			line = br.readLine();
			while (line != null) {
				String[] data = line.split(";");
				MonsterDefinition def = new MonsterDefinition(data[0]);
				def.setAppearance(AppearanceFactory.getAppearanceFactory().getAppearance(data[1]));
				def.setDescription(data[2]);
				def.setLongDescription(data[3]);
				def.setWavOnHit(data[4]);
				def.setBloodContent(Integer.parseInt(data[5]));
				def.setUndead(data[6].equals("true"));
				def.setEthereal(data[7].equals("true"));
				def.setCanSwim(data[8].equals("true"));
				def.setCanFly(data[9].equals("true"));
				def.setScore(Integer.parseInt(data[10]));
				def.setSightRange(Integer.parseInt(data[11]));
				def.setMaxHits(Integer.parseInt(data[12]));
				def.setAttack(Integer.parseInt(data[13]));
				def.setWalkCost(Integer.parseInt(data[14]));
				def.setAttackCost(Integer.parseInt(data[15]));
				def.setEvadeChance(Integer.parseInt(data[16]));
				def.setEvadeMessage(data[17]);
				def.setAutorespawnCount(Integer.parseInt(data[18]));

				vecMonsters.add(def);
				line = br.readLine();
			}
			return vecMonsters.toArray(new MonsterDefinition[0]);
		} catch (IOException ioe) {
			throw new CRLException("Error while loading data from monster file");
		} finally {
			try {
				br.close();
			} catch (IOException ioe) {
				throw new CRLException("Error while loading data from monster file");
			}
		}
	}

	public static MonsterDefinition[] getMonsterDefinitions(String monsterDefFile, String monsterXMLAIFile)
			throws CRLException {
		try {
			MonsterDefinition[] monsters = getBaseMonsters(monsterDefFile);
			HashMap<String, MonsterDefinition> hashMonsters = new HashMap<>();
			for (MonsterDefinition monster : monsters) {
				hashMonsters.put(monster.getID(), monster);
			}

			MonsterContentHandler handler2 = new MonsterContentHandler(hashMonsters);

			XMLReader reader = new ParserAdapter(new MinML());
			DESEncrypter encrypter = new DESEncrypter("65csvlk3489585f9rjh");
			reader.setContentHandler(handler2);
			reader.parse(new InputSource(encrypter.decrypt(Files.newInputStream(Paths.get(monsterXMLAIFile)))));

			return monsters;

		} catch (IOException ioe) {
			throw new CRLException("Error while loading data from monster file");
		} catch (SAXException sax) {
			sax.printStackTrace();
			throw new CRLException("Error while loading data from monster file");
		}
	}

}
