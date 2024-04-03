package crl.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

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
import crl.monster.MonsterDefinition;

public class MonsterContentHandler implements ContentHandler {
	private HashMap<String, MonsterDefinition> hashMonsters;

	MonsterContentHandler(HashMap<String, MonsterDefinition> hashMonsters) {
		this.hashMonsters = hashMonsters;
	}

	private MonsterDefinition currentMD;
	private ActionSelector currentSelector;
	private ArrayList<RangedAttack> currentRangedAttacks;

	@Override
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes at) throws SAXException {
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
			currentRangedAttacks = new ArrayList<>(10);
			break;
		case "rangedAttack":
			int damage = 0;
			try {
				damage = Integer.parseInt(at.getValue("damage"));
			} catch (NumberFormatException nfe) {
				//Do nothing
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

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("rangedAttacks")) {
			((MonsterAI) currentSelector).setRangedAttacks(currentRangedAttacks);
		} else if (localName.equals("selector")) {

			currentMD.setDefaultSelector(currentSelector);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}

	private int inte(String s) {
		return Integer.parseInt(s);
	}

}
