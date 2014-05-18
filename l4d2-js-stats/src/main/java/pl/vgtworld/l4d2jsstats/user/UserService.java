package pl.vgtworld.l4d2jsstats.user;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import pl.vgtworld.l4d2jsstats.user.dto.UserDto;

@Stateless
public class UserService {
	
	private static final String USER_COOKIE_NAME = "user";
	
	private static final String TOKEN_COOKIE_NAME = "token";
	
	@Inject
	private UserDao dao;
	
	@Inject
	private UserTokenService userTokenService;
	
	public void createNewUser(String login, String password) throws UserServiceException {
		try {
			String salt = UserUtils.generateSalt();
			String passwordHash = UserUtils.generatePasswordHash(password, salt);
			User user = new User();
			user.setLogin(login);
			user.setPassword(passwordHash);
			user.setSalt(salt);
			user.setActive(false);
			user.setCreatedAt(new Date());
			dao.add(user);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new UserServiceException("Enexpected error while trying to create user.", e);
		}
	}
	
	public boolean isLoginAvailable(String login) {
		return dao.findByLogin(login) == null;
	}
	
	public boolean isCorrectLoginCredentials(String login, String password)
		throws NoSuchAlgorithmException, UnsupportedEncodingException {
		User user = dao.findByLogin(login);
		if (user == null) {
			return false;
		}
		String hash = UserUtils.generatePasswordHash(password, user.getSalt());
		if (!hash.equals(user.getPassword())) {
			return false;
		}
		return true;
	}
	
	public UserDto findByLogin(String login) {
		User user = dao.findByLogin(login);
		if (user == null) {
			return null;
		}
		return mapToUserLoginDto(user);
	}
	
	public void login(String login, String remoteAddress, HttpServletResponse response) throws UserServiceException {
		try {
			User user = dao.findByLogin(login);
			String token = userTokenService.createNewToken(user, remoteAddress);
			Cookie userCookie = new Cookie(USER_COOKIE_NAME, login);
			Cookie tokenCookie = new Cookie(TOKEN_COOKIE_NAME, token);
			response.addCookie(userCookie);
			response.addCookie(tokenCookie);
		} catch (UserTokenServiceException e) {
			throw new UserServiceException("Error while creating login token.", e);
		}
	}
	
	public void logout(HttpServletResponse response) {
		Cookie userCookie = new Cookie(USER_COOKIE_NAME, null);
		Cookie tokenCookie = new Cookie(TOKEN_COOKIE_NAME, null);
		response.addCookie(userCookie);
		response.addCookie(tokenCookie);
	}
	
	public UserDto validateLoginCookies(Cookie userName, Cookie userToken) {
		String name = userName.getValue();
		String token = userToken.getValue();
		User user = dao.findByLogin(name);
		if (user == null) {
			return null;
		}
		String storedToken = userTokenService.findTokenForUser(user);
		if (storedToken == null) {
			return null;
		}
		if (storedToken.equals(token)) {
			return mapToUserLoginDto(user);
		}
		return null;
	}
	
	private UserDto mapToUserLoginDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setLogin(user.getLogin());
		dto.setActive(user.isActive());
		dto.setCreatedAt(user.getCreatedAt());
		return dto;
	}
	
}
