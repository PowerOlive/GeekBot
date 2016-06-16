package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

/**
 * Fetches a random blague from la-blague-du-jour.com (French)
 * 
 * 
 */
@BotCommand
public class BlagueCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	Logger log;

	@Trigger("!blague")
	@Help("Fetches a random blague from blaguesdemerde.fr")
	public List<String> getRandomBlague() {
		List<String> result = new ArrayList<String>();
		String url = "http://www.blaguesdemerde.fr/blagues-aleatoires";
		try {
			Document doc = Jsoup.parse(utilsService.getContent(url));

			String blague = doc.select(".joke_text_contener").get(0).text();
			List<String> lines = Arrays.asList(blague.split("<br />"));
			for (String line : lines) {
				result.add(StringEscapeUtils.unescapeHtml4(line));
			}

			if (result.size() > 5) {
				// joke too long
				return getRandomBlague();
			}

			result.add(0, IRCUtils.bold("Mega Vanne"));
			result.add(IRCUtils.bold("http://www.instantrimshot.com/"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.add("Could not contact " + url);
		}

		return result;
	}
}
