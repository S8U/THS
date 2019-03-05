package su.plugin.buyrank.api.manager;

import java.util.HashMap;
import lombok.Getter;
import su.plugin.buyrank.api.object.Rank;

@Getter
public class RankManager {
	
	private HashMap<String, Rank> ranks = new HashMap<>();
	
	public void setRank(String name, Rank rank) {
		ranks.put(name.toLowerCase(), rank);
	}
	
	public void deleteRank(String name) {
		ranks.remove(name.toLowerCase());
	}
	
	public boolean existRank(String name) {
		return ranks.containsKey(name.toLowerCase());
	}
	
	public Rank getRank(String name) {
		return ranks.get(name.toLowerCase());
	}
	
}
