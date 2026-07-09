package com.uca.service;

import com.uca.entity.Branch;
import com.uca.entity.Order;
import com.uca.entity.Role;
import com.uca.entity.User;
import com.uca.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderService orderService;

    private User managerUser;
    private Order orderFromSameBranch;
    private Order orderFromDifferentBranch;
    private Branch branch1;
    private Branch branch2;

    @BeforeEach
    void setUp() {
        branch1 = new Branch(1L, "Sucursal Norte", "Norte");
        branch2 = new Branch(2L, "Sucursal Sur", "Sur");

        managerUser = new User();
        managerUser.setId(1L);
        managerUser.setUsername("manager1");
        managerUser.setRole(Role.MANAGER);
        managerUser.setBranch(branch1); // Manager of branch 1

        orderFromSameBranch = new Order();
        orderFromSameBranch.setId(100L);
        orderFromSameBranch.setBranch(branch1);

        orderFromDifferentBranch = new Order();
        orderFromDifferentBranch.setId(101L);
        orderFromDifferentBranch.setBranch(branch2);
    }

    private void mockAuthenticatedUser(User user) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void managerCanAccessOrderFromSameBranch() {
        // Arrange
        mockAuthenticatedUser(managerUser);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(orderFromSameBranch));

        // Act
        Order result = orderService.getOrderById(100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void managerCannotAccessOrderFromDifferentBranch() {
        // Arrange
        mockAuthenticatedUser(managerUser);
        when(orderRepository.findById(101L)).thenReturn(Optional.of(orderFromDifferentBranch));

        // Act & Assert
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrderById(101L)
        );
        assertEquals("Manager can only manage orders of their own branch.", exception.getMessage());
    }

    @Test
    void adminCanAccessAnyOrder() {
        // Arrange
        User adminUser = new User();
        adminUser.setRole(Role.ADMIN);
        mockAuthenticatedUser(adminUser);
        when(orderRepository.findById(101L)).thenReturn(Optional.of(orderFromDifferentBranch));

        // Act
        Order result = orderService.getOrderById(101L);

        // Assert
        assertNotNull(result);
        assertEquals(101L, result.getId());
    }
}
