package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private UserService userService;
    @Mock
    private SecurityService securityService;

    @Mock
    private UserDao userDao;

    @Mock
    private User user;

    @BeforeEach
    public void setUp(){userService = new UserServiceImpl(userDao,securityService);
    }

    @Test
    public void userShouldGetNewPassword() throws Exception{
        String fakePassword = "password";
        String hashedFakePAssword = "Hashe password";
        when(user.getPassword()).thenReturn(fakePassword);
        when(securityService.hash(fakePassword)).thenReturn(hashedFakePAssword);

        User userReturn = userService.assignPassword(user);

        verify(securityService).hash(fakePassword);
        verify(user).setPassword(hashedFakePAssword);
        verify(userDao).updateUser(user);

        assertEquals(user, userReturn);
    }

    @Test
    public void shouldNotProceedFutherWhenUserGetPasswordThrowsException() {
        when(user.getPassword()).thenThrow(RuntimeException.class);

        Exception ex = assertThrows(RuntimeException.class, () -> {
            userService.assignPassword(user);
        });

        assertNotNull(ex);
        verify(user, times(1)).getPassword();
        verifyNoMoreInteractions(user);
        verifyNoInteractions(userDao);

    }

    @Test
    public void shouldNotProceedFutherWhenSecurityServiceThrowsException() throws Exception{
        when(securityService.hash(anyString())).thenThrow(RuntimeException.class);

        Exception ex = assertThrows(RuntimeException.class, () -> {
            securityService.hash(anyString());
        });

        assertNotNull(ex);
        verifyNoMoreInteractions(user);
        verifyNoInteractions(userDao);

    }

    @Test
    public void shouldNotProceedFutherWhenUserSetPasswordThrowsException() throws Exception{

    when(user.getPassword()).thenReturn("password");
    when(securityService.hash("password")).thenReturn("hashed Password");
    doThrow(IllegalArgumentException.class).when(user).setPassword("hashed Password");

    Exception ex = assertThrows(IllegalArgumentException.class, () -> {
        userService.assignPassword(user);
    });

    assertNotNull(ex);
    verify(user).getPassword();
    verify(securityService).hash((anyString()));
    verify(user).setPassword((anyString()));
    verifyNoInteractions(userDao);
    }

    @Test
    public void shouldNotUpdateUserDaoWhenExceptionIsThrownOnUpdateUser() throws Exception {
        doThrow(RuntimeException.class).when(userDao).updateUser(any());

        Exception ex = assertThrows(RuntimeException.class, () -> {
                    userDao.updateUser(any());
                }
        );

        assertNotNull(ex);

        verify(userDao,times(1)).updateUser(any());

    }

    @Test
    public void shouldThrowNullPointerExceptionWhenNullIsPassedAsParameter() {

     Exception ex = assertThrows(NullPointerException.class, () -> {
         userService.assignPassword(null);
     });
     assertNotNull(ex);
     verifyNoInteractions(securityService);
     verifyNoInteractions(userDao);
     verifyNoInteractions(user);
    }
}
