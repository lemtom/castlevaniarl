package crl;

import java.lang.reflect.Field;
import java.util.Properties;

import crl.action.Action;
import crl.action.ActionFactory;
import crl.action.monster.Dash;
import crl.action.monster.MandragoraScream;
import crl.action.monster.MonsterCharge;
import crl.action.monster.MonsterMissile;
import crl.action.monster.MonsterWalk;
import crl.action.monster.SummonMonster;
import crl.action.monster.Swim;
import crl.action.monster.boss.MummyStrangle;
import crl.action.monster.boss.MummyTeleport;
import crl.action.monster.boss.Teleport;
import crl.ai.ActionSelector;
import crl.ai.SelectorFactory;
import crl.ai.monster.BasicMonsterAI;
import crl.ai.monster.RangedAI;
import crl.ai.monster.UnderwaterAI;
import crl.ai.monster.WanderToPlayerAI;
import crl.ai.npc.PriestAI;
import crl.ai.npc.VillagerAI;
import crl.ai.player.WildMorphAI;
import crl.conf.console.data.CharAppearances;
import crl.conf.gfx.data.GFXAppearances;
import crl.conf.gfx.data.GFXConfiguration;
import crl.data.Cells;
import crl.data.Features;
import crl.data.Items;
import crl.data.MonsterLoader;
import crl.data.NPCs;
import crl.data.SmartFeatures;
import crl.feature.CountDown;
import crl.feature.FeatureFactory;
import crl.feature.SmartFeatureFactory;
import crl.feature.ai.BlastCrystalAI;
import crl.feature.ai.CrossAI;
import crl.feature.ai.FlameAI;
import crl.feature.ai.NullSelector;
import crl.game.CRLException;
import crl.item.ItemFactory;
import crl.level.MapCellFactory;
import crl.monster.MonsterFactory;
import crl.npc.NPCDefinition;
import crl.npc.NPCFactory;
import crl.ui.Appearance;
import crl.ui.AppearanceFactory;
import sz.csi.CharKey;

public class Helper {
	static int i(String s) {
		return Integer.parseInt(s);
	}

	static String readKeyString(Properties config, String keyName) {
		return readKey(config, keyName) + "";
	}

	private static int readKey(Properties config, String keyName) {
		String fieldName = config.getProperty(keyName).trim();
		if (fieldName == null)
			throw new RuntimeException("Invalid key.cfg file, property not found: " + keyName);
		try {
			Field field = CharKey.class.getField(fieldName);
			return field.getInt(CharKey.class);
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading field : " + fieldName);
		}
	}

	static void initializeGAppearances(GFXConfiguration gfx_configuration) {
		Appearance[] definitions = new GFXAppearances(gfx_configuration).getAppearances();
		for (Appearance definition : definitions) {
			AppearanceFactory.getAppearanceFactory().addDefinition(definition);
		}
	}

	static void initializeCAppearances() {
		Appearance[] definitions = new CharAppearances().getAppearances();
		for (Appearance definition : definitions) {
			AppearanceFactory.getAppearanceFactory().addDefinition(definition);
		}
	}

	static void initializeActions() {
		ActionFactory af = ActionFactory.getActionFactory();
		Action[] definitions = new Action[] { new Dash(), new MonsterWalk(), new Swim(), new MonsterCharge(),
				new MonsterMissile(), new SummonMonster(), new MummyStrangle(), new MummyTeleport(), new Teleport(),
				new MandragoraScream() };
		for (Action definition : definitions)
			af.addDefinition(definition);
	}

	static void initializeCells() {
		MapCellFactory.getMapCellFactory().init(Cells.getCellDefinitions(AppearanceFactory.getAppearanceFactory()));
	}

	static void initializeFeatures() {
		FeatureFactory.getFactory().init(Features.getFeatureDefinitions(AppearanceFactory.getAppearanceFactory()));
	}

	static void initializeSelectors() {
		ActionSelector[] definitions = getSelectorDefinitions();
		for (ActionSelector definition : definitions) {
			SelectorFactory.getSelectorFactory().addDefinition(definition);
		}
	}

	static void initializeMonsters() throws CRLException {

		MonsterFactory.getFactory()
				.init(MonsterLoader.getMonsterDefinitions("data/monsters.ecsv", "data/monsters.exml"));
	}

	static void initializeNPCs() {
		NPCDefinition[] definitions = NPCs.getNPCDefinitions();
		NPCFactory npcf = NPCFactory.getFactory();
		for (NPCDefinition definition : definitions) {
			npcf.addDefinition(definition);
		}
	}

	static void initializeItems() {
		ItemFactory.getItemFactory().init(Items.getItemDefinitions());
	}

	static void initializeSmartFeatures() {
		SmartFeatureFactory.getFactory().init(SmartFeatures.getSmartFeatures(SelectorFactory.getSelectorFactory()));
	}

	static ActionSelector[] getSelectorDefinitions() {
		return new ActionSelector[] { new WanderToPlayerAI(), new UnderwaterAI(), new RangedAI(), new FlameAI(),
				new CrossAI(), new BlastCrystalAI(), new CountDown(), new VillagerAI(), new PriestAI(),
				new NullSelector(), new BasicMonsterAI(), new WildMorphAI() };
	}

}
