package org.icemoon.eartheternal.common;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "stat-update", mixinStandardHelpOptions = true, version = "StatUpdate 0.1")
public class StatUpdate implements Runnable {
	static Logger LOG = Logger.getGlobal();

	@Option(names = { "-f",
			"--flags" }, description = "Include only spawns in the specified zones that have this flag. "
					+ "Multiple -f options for multiple flags.")
	private char[] flags = new char[0];

	@Option(names = { "-z", "--zone" }, description = "Include only creatures in specific zones. "
			+ "Multiple -z options for multiple zones.")
	private int[] zones = new int[0];

	@Option(names = { "-m",
			"--match" }, description = "Include only creatures whose attributes match this expression. The value is an expression using SPEL (Spring Expression Language)")
	private String[] match = new String[0];

	@Option(names = { "-u",
			"--update" }, description = "Update a stat. The value is an expression using SPEL (Spring Expression Language).")
	private String[] update = new String[0];

	@Option(required = true, names = { "-d", "--data" }, description = "Location to data.")
	private String data;

	public StatUpdate() {
	}

	public static void main(String[] args) {
		CommandLine.run(new StatUpdate(), args);
	}

	@Override
	public void run() {

		File worldDir = new File(data);
		IDatabase db = new DefaultDatabase(worldDir);
		World<IDatabase> world = db.getWorld();

		Set<Long> creatures = new LinkedHashSet<>();

		LOG.info("Gathering ... ");

		if (zones.length == 0) {
			creatures.addAll(db.getCreatures().keySet());
		} else {
			for (long zone : zones) {
				Sceneries<IDatabase> w = world.get(zone);
				if (w == null)
					LOG.severe(String.format("No such zone %d", zone));
				else {
					for (Scenery<IDatabase> b : w.values()) {
						if (StringUtils.isNotBlank(b.getSpawnPackage())) {
							if (b.getSpawnPackage().startsWith("#")) {
								StringBuilder br = new StringBuilder();
								Set<Character> flags = new HashSet<>();
								for (char c : b.getSpawnPackage().toCharArray()) {
									if (Character.isDigit(c))
										br.append(c);
									else
										flags.add(c);
								}
								if (this.flags.length == 0) {
									creatures.add(Long.parseLong(br.toString()));
								} else {
									for (char c : this.flags) {
										if (flags.contains(c)) {
											creatures.add(Long.parseLong(br.toString()));
											break;
										}
									}
								}
							} else {
								SpawnPackage pkg = db.getSpawnPackages().get(b.getSpawnPackage());
								// TODO check flags
								for (SpawnEntry se : pkg.getSpawns())
									creatures.add(se.getCreatureId());
							}
						}
					}
				}
			}
		}

		LOG.info("Filtering ... ");

		Set<Creature> filteredCreatures = new LinkedHashSet<>();
		for (Long l : creatures) {
			Creature c = db.getCreatures().get(l);
			if (c == null)
				LOG.warning(String.format("No creature with ID %d", l));
			else {
				boolean ok = false;

				/* Level */
				if (match.length > 0) {

					ExpressionParser parser = new SpelExpressionParser();
					StandardEvaluationContext context = new StandardEvaluationContext(c);
					for (String m : match) {
						Expression exp = parser.parseExpression(m);
						Boolean val = exp.getValue(context, Boolean.class);
						if(Boolean.TRUE.equals(val)) {
							ok = true;
							break;
						}
					}
					
				} else
					ok = true;
				if (ok)
					filteredCreatures.add(c);
			}
		}

		LOG.info("Altering ... ");
		try {

			for (Creature c : filteredCreatures) {
				LOG.info(String.format("%-40s @ %2d : %-10s %-10s", c.getDisplayName(), c.getLevel(), c.getProfession(),
						c.getRarity()));
				ExpressionParser parser = new SpelExpressionParser();
				StandardEvaluationContext context = new StandardEvaluationContext(c);
				for (String u : update) {
					Expression exp = parser.parseExpression(u);
					Object val = exp.getValue(context);
					LOG.info(String.format("   %s = %s", u, val));
				}
				db.getCreatures().save(c);
			}
		} catch (Exception e) {
			throw new IllegalStateException("Failed to evaluate expression.", e);
		}

	}
}
