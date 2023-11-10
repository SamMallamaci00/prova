package org.example;

public class UserServiceImpl implements UserService{

    private UserDao userDao;
    private SecurityService securityService;

    public UserServiceImpl(UserDao userDao, SecurityService securityService) {
        this.userDao = userDao;
        this.securityService = securityService;
    }

    public User assignPassword(User user) throws Exception{
        String password = securityService.hash(user.getPassword());
        user.setPassword(password);
        userDao.updateUser(user);
        return user;
    }
}
