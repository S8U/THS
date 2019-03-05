package su.plugin.core.bukkit.api.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import lombok.Getter;
import lombok.Setter;

public class SideBar {
	
	@Setter
	@Getter
	private String title;
	
	@Setter
	@Getter
	private List<String> texts = new ArrayList<>();
	
	@Setter
	@Getter
	private Scoreboard board;
	private Objective obj;
	
	public SideBar(String title) {
		this.title = title;
	}
	
	public void setText(int line, String text) {
		texts.set(line, text);
	}
	
	public void addText(String text) {
		texts.add(text);
	}
	
	public String getText(int line) {
		return texts.get(line);
	}
	
	public Scoreboard updateScoreboard() {
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		obj = board.registerNewObjective("usidebar", "usidebar");
		
		obj.setDisplayName(title);
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		for(int i = 0; i < texts.size(); i++) {
			String text = texts.get(i);
			
			Team team = board.getTeam(String.valueOf(i));
			team = team == null ? board.registerNewTeam(String.valueOf(i)) : team;
			
			String e = null;

			if(text.length() > 48) {
				text = text.substring(0, 48);
			}

			if(text.length() <= 16) {
				team.addEntry(e = text);
			} else if(text.length() > 16 && text.length() <= 32) {
				team.setPrefix(text.substring(0, 16));
				team.addEntry(e = text.substring(16, text.length()));
			} else if(text.length() > 32) {
				team.setPrefix(text.substring(0, 16));
				team.setSuffix(text.substring(32, 48));
				team.addEntry(e = text.substring(16, 32));
			}
			
			obj.getScore(e).setScore(texts.size() - i);
		}
		
		return board;
	}
	
}