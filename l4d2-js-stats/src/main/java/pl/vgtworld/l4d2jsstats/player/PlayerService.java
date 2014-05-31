package pl.vgtworld.l4d2jsstats.player;

import javax.ejb.Stateless;
import javax.inject.Inject;

import pl.vgtworld.l4d2jsstats.match.Match;
import pl.vgtworld.l4d2jsstats.match.MatchDao;
import pl.vgtworld.l4d2jsstats.player.dto.PlayerCampaignDto;
import pl.vgtworld.l4d2jsstats.user.User;
import pl.vgtworld.l4d2jsstats.user.UserDao;

@Stateless
public class PlayerService {
	
	@Inject
	private PlayerDao playerDao;
	
	@Inject
	private PlayerCampaignDao playerCampaignDao;
	
	@Inject
	private MatchDao matchDao;
	
	@Inject
	private UserDao userDao;
	
	public void addUserToCampaignMatch(int matchId, int userId, boolean survived, int deathCount)
		throws PlayerServiceException {
		Match match = matchDao.findById(matchId);
		User user = userDao.findById(userId);
		if (match == null) {
			throw new PlayerServiceException("Can't find match with specified ID.");
		}
		if (user == null) {
			throw new PlayerServiceException("Can't find user with specified ID.");
		}
		
		Player player = new Player();
		player.setMatch(match);
		player.setUser(user);
		playerDao.add(player);
		
		PlayerCampaign campaignPlayer = new PlayerCampaign();
		campaignPlayer.setPlayer(player);
		campaignPlayer.setSurvived(survived);
		campaignPlayer.setDeaths(deathCount);
		playerCampaignDao.add(campaignPlayer);
	}
	
	public void deleteUserFromMatch(int userId, int matchId) {
		playerDao.delete(userId, matchId);
	}
	
	public PlayerCampaignDto[] findPlayersFromMatch(int matchId) {
		PlayerCampaign[] players = playerCampaignDao.findByMatch(matchId);
		PlayerCampaignDto[] dtoList = new PlayerCampaignDto[players.length];
		for (int i = 0; i < players.length; ++i) {
			dtoList[i] = mapFrom(players[i]);
		}
		return dtoList;
	}
	
	private PlayerCampaignDto mapFrom(PlayerCampaign player) {
		PlayerCampaignDto dto = new PlayerCampaignDto();
		dto.setUserId(player.getPlayer().getUser().getId());
		dto.setName(player.getPlayer().getUser().getLogin());
		dto.setSurvived(player.isSurvived());
		dto.setDeaths(player.getDeaths());
		return dto;
	}
}
