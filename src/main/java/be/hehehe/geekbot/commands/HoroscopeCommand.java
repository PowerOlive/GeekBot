package be.hehehe.geekbot.commands;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Maps;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.State;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.DiscordUtils;
import lombok.extern.jbosslog.JBossLog;

/**
 * Horoscope from astrocenter.fr (French)
 * 
 */
@BotCommand
@JBossLog
public class HoroscopeCommand {

	@Inject
	State state;

	@Inject
	BotUtilsService utilsService;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void init() {
		Map<String, String> mapping = state.get(Map.class);
		if (mapping == null) {
			mapping = Maps.newLinkedHashMap();
			mapping.put("belier", "belier");
			mapping.put("taureau", "taureau");
			mapping.put("gemeaux", "gemeaux");
			mapping.put("cancer", "cancer");
			mapping.put("lion", "lion");
			mapping.put("vierge", "vierge");
			mapping.put("balance", "balance");
			mapping.put("scorpion", "scorpion");
			mapping.put("sagittaire", "sagittaire");
			mapping.put("capricorne", "capricorne");
			mapping.put("verseau", "verseau");
			mapping.put("poissons", "poissons");
			state.put(mapping);
		}
	}

	@Trigger(value = "!horoscope", type = TriggerType.EXACTMATCH)
	@Help("Prints help on how to use this command.")
	public String getHoroscopeHelp() {
		String availableSigns = StringUtils.join(state.get(Map.class).keySet(), ", ");
		return DiscordUtils.bold("!horoscope <signe>") + " - Available signs : " + availableSigns;
	}

	@SuppressWarnings("unchecked")
	@Trigger(value = "!horoscope", type = TriggerType.STARTSWITH)
	public String getHoroscope(TriggerEvent event) {
		String sign = event.getMessage();
		if ("poisson".equals(sign)) {
			sign = "poissons";
		}

		String content = null;
		String line = null;
		try {
			Map<String, String> mapping = state.get(Map.class);
			String id = mapping.get(sign);
			content = utilsService.getContent(String.format("http://mon.astrocenter.fr/horoscope/quotidien/%s", id));
			Document doc = Jsoup.parse(content);

			Element horo = doc.select(".article-horoscope").first().child(0);
			line = horo.text();

		} catch (Exception e) {
			log.error("Could not parse HTML" + e.getMessage() + SystemUtils.LINE_SEPARATOR + content, e);
		}

		return line;
	}
}