package crl.data;

import crl.ai.ActionSelector;
import crl.ai.monster.BasicMonsterAI;
import crl.ai.monster.MonsterAI;
import crl.ai.monster.RangedAI;
import crl.ai.monster.RangedAttack;
import crl.ai.monster.StationaryAI;
import crl.ai.monster.UnderwaterAI;
import crl.ai.monster.WanderToPlayerAI;
import crl.ai.monster.boss.DeathAI;
import crl.ai.monster.boss.DemonDraculaAI;
import crl.ai.monster.boss.DraculaAI;
import crl.ai.monster.boss.FrankAI;
import crl.ai.monster.boss.MedusaAI;
import crl.game.CRLException;
import crl.monster.*;
import crl.ui.AppearanceFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sz.crypt.*;
import uk.co.wilson.xml.MinML;

public class MonsterLoader {
	public static MonsterDefinition[] getBaseMonsters(String monsterFile) throws CRLException {
		BufferedReader br = null;
		try {
			Vector<MonsterDefinition> vecMonsters = new Vector<>(10);
			DESEncrypter encrypter = new DESEncrypter("65csvlk3489585f9rjh");
			br = new BufferedReader(new InputStreamReader(encrypter.decrypt(Files.newInputStream(Paths.get(monsterFile)))));
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
			Hashtable<String, MonsterDefinition> hashMonsters = new Hashtable<>();
			for (MonsterDefinition monster : monsters) {
				hashMonsters.put(monster.getID(), monster);
			}

			MonsterDocumentHandler handler = new MonsterDocumentHandler(hashMonsters);
			MinML parser = new MinML();
			DESEncrypter encrypter = new DESEncrypter("65csvlk3489585f9rjh");
			// parser.setContentHandler(handler);
			parser.setDocumentHandler(handler);
			parser.parse(new InputSource(encrypter.decrypt(Files.newInputStream(Paths.get(monsterXMLAIFile)))));
			return monsters;

			/*
			 * Print Some data to a CSV File, yeah I am evil BufferedWriter write =
			 * FileUtil.getWriter("monsterStats.csv"); for (int i = 0; i < ret.length; i++){
			 * write.write(ret[i].getID()+","+ret[i].getAppearance().getID()+","+ret[i].
			 * getDescription()+",,"+ ret[i].getWavOnHit()+","+ret[i].getBloodContent()+","+
			 * ret[i].isUndead()+","+ret[i].isEthereal()+","+
			 * ret[i].isCanSwim()+",false,"+ret[i].getScore()+","+
			 * ret[i].getSightRange()+","+ret[i].getMaxHits()+","+
			 * ret[i].getAttack()+","+ret[i].getWalkCost()+","+ret[i].getAttackCost()+","+
			 * ret[i].getEvadeChance()+","+ret[i].getEvadeMessage()+","+ret[i].
			 * getAutorespawnCount() ); write.write("\n"); } write.close();
			 */
			// End Print Some data to a CSV File, yeah I am evil

		} catch (IOException ioe) {
			throw new CRLException("Error while loading data from monster file");
		} catch (SAXException sax) {
			sax.printStackTrace();
			throw new CRLException("Error while loading data from monster file");
		}
	}

}

class MonsterDocumentHandler implements DocumentHandler {
	private Hashtable<String, MonsterDefinition> hashMonsters;

	MonsterDocumentHandler(Hashtable<String, MonsterDefinition> hashMonsters) {
		this.hashMonsters = hashMonsters;
	}

	private MonsterDefinition currentMD;
	private ActionSelector currentSelector;
	private Vector<RangedAttack> currentRangedAttacks;

	public void startDocument() throws org.xml.sax.SAXException {
	}

	public void startElement(String localName, AttributeList at) throws org.xml.sax.SAXException {
        switch (localName) {
            case "monster":
                currentMD = hashMonsters.get(at.getValue("id"));
                break;
            case "sel_wander":
                currentSelector = new WanderToPlayerAI();
                break;
            case "sel_underwater":
                currentSelector = new UnderwaterAI();
                break;
            case "sel_sickle":
                currentSelector = new crl.action.monster.boss.SickleAI();
                break;
            case "sel_death":
                currentSelector = new DeathAI();
                break;
            case "sel_dracula":
                currentSelector = new DraculaAI();
                break;
            case "sel_demondracula":
                currentSelector = new DemonDraculaAI();
                break;
            case "sel_medusa":
                currentSelector = new MedusaAI();
                break;
            case "sel_frank":
                currentSelector = new FrankAI();
                break;
            case "sel_stationary":
                currentSelector = new StationaryAI();
                break;
            case "sel_basic":
                currentSelector = new BasicMonsterAI();
                if (at.getValue("stationary") != null)
                    ((BasicMonsterAI) currentSelector).setStationary(at.getValue("stationary").equals("true"));
                if (at.getValue("approachLimit") != null)
                    ((BasicMonsterAI) currentSelector).setApproachLimit(inte(at.getValue("approachLimit")));
                if (at.getValue("waitPlayerRange") != null)
                    ((BasicMonsterAI) currentSelector).setWaitPlayerRange(inte(at.getValue("waitPlayerRange")));
                if (at.getValue("patrolRange") != null)
                    ((BasicMonsterAI) currentSelector).setPatrolRange(inte(at.getValue("patrolRange")));
                break;
            case "sel_ranged":
                currentSelector = new RangedAI();
                ((RangedAI) currentSelector).setApproachLimit(inte(at.getValue("approachLimit")));
                break;
            case "rangedAttacks":
                currentRangedAttacks = new Vector<>(10);
                break;
            case "rangedAttack":
                int damage = 0;
                try {
                    damage = Integer.parseInt(at.getValue("damage"));
                } catch (NumberFormatException nfe) {

                }

                RangedAttack ra = new RangedAttack(at.getValue("id"), at.getValue("type"), at.getValue("status_effect"),
                        Integer.parseInt(at.getValue("range")), Integer.parseInt(at.getValue("frequency")),
                        at.getValue("message"), at.getValue("effectType"), at.getValue("effectID"), damage

                        // color
                );
                if (at.getValue("effectWav") != null)
                    ra.setEffectWav(at.getValue("effectWav"));
                if (at.getValue("summonMonsterId") != null)
                    ra.setSummonMonsterId(at.getValue("summonMonsterId"));
                if (at.getValue("charge") != null)
                    ra.setChargeCounter(inte(at.getValue("charge")));

                currentRangedAttacks.add(ra);
                break;
        }
	}

	public void endElement(String localName) throws org.xml.sax.SAXException {
		if (localName.equals("rangedAttacks")) {
			((MonsterAI) currentSelector).setRangedAttacks(currentRangedAttacks);
		} else if (localName.equals("selector")) {

			currentMD.setDefaultSelector(currentSelector);
		}

	}

	public void characters(char[] values, int param, int param2) throws org.xml.sax.SAXException {
	}

	public void endDocument() throws org.xml.sax.SAXException {
	}

	public void endPrefixMapping(String str) throws org.xml.sax.SAXException {
	}

	public void ignorableWhitespace(char[] values, int param, int param2) throws org.xml.sax.SAXException {
	}

	public void processingInstruction(String str, String str1) throws org.xml.sax.SAXException {
	}

	public void setDocumentLocator(org.xml.sax.Locator locator) {
	}

	public void skippedEntity(String str) throws org.xml.sax.SAXException {
	}

	public void startPrefixMapping(String str, String str1) throws org.xml.sax.SAXException {
	}

	private int inte(String s) {
		return Integer.parseInt(s);
	}
}
