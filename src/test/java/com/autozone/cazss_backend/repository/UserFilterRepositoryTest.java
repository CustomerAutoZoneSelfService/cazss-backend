package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
public class UserFilterRepositoryTest {

  @Autowired private UserFilterRepository userFilterRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ResponsePatternRepository responsePatternRepository;

  @Autowired private CategoryRepository categoryRepository;

  private UserEntity user;
  private CategoryEntity category;
  private ResponsePatternEntity responsePattern;

  @BeforeEach
  public void setUp() {
    // Crear un correo electrónico único para evitar duplicidad
    String email = "test" + System.currentTimeMillis() + "@example.com";

    // Crear usuario
    user = new UserEntity();
    user.setEmail(email);
    user.setActive(true);
    user.setRole(UserRoleEnum.USER);
    user = userRepository.save(user);

    // Crear categoría con un nombre único
    category = new CategoryEntity();
    category.setName("Test Category " + System.currentTimeMillis()); // Nombre único con timestamp
    category.setColor("#FFFFFF");
    category = categoryRepository.save(category);

    // Crear patrón de respuesta con un valor no nulo para "pattern"
    responsePattern = new ResponsePatternEntity();
    responsePattern.setName("Test Pattern");
    responsePattern.setDescription("Test Pattern Description");
    responsePattern.setPattern("ValidPattern123");
    responsePattern.setParentId(null);
    responsePattern.setIsLeaf(true);
    responsePattern = responsePatternRepository.save(responsePattern);
  }

  @Test
  public void givenUserFilterRepository_whenSavedAndRetrieved_thenOK() {
    // Crear UserFilter con los valores apropiados
    UserFilterEntity userFilter = new UserFilterEntity();

    // Crear la clave compuesta
    UserFilterEntity.UserFilterId userFilterId =
        new UserFilterEntity.UserFilterId(user.getUserId(), responsePattern.getResponsePatternId());
    userFilter.setId(userFilterId); // Asignar la clave compuesta

    // Asignar usuario y patrón de respuesta
    userFilter.setUser(user);
    userFilter.setResponsePattern(responsePattern);

    // Guardar en el repositorio
    userFilter = userFilterRepository.save(userFilter);

    // Verificar que se ha guardado correctamente
    assertNotNull(userFilter.getId());
    assertEquals(user.getUserId(), userFilter.getUser().getUserId());
    assertEquals(
        responsePattern.getResponsePatternId(),
        userFilter.getResponsePattern().getResponsePatternId());
  }

  @Test
  public void givenUserFilterRepository_whenUpdated_thenOK() {
    // Crear y guardar un UserFilter
    UserFilterEntity userFilter = new UserFilterEntity();
    UserFilterEntity.UserFilterId userFilterId =
        new UserFilterEntity.UserFilterId(user.getUserId(), responsePattern.getResponsePatternId());
    userFilter.setId(userFilterId); // Asignar la clave compuesta
    userFilter.setUser(user);
    userFilter.setResponsePattern(responsePattern);
    userFilter = userFilterRepository.save(userFilter);

    // Actualizar el nombre del patrón de respuesta
    responsePattern.setName("Updated Pattern Name");

    // Guardar el patrón de respuesta actualizado
    responsePattern = responsePatternRepository.save(responsePattern);

    // Verificar que el patrón se actualizó correctamente
    assertEquals("Updated Pattern Name", responsePattern.getName());

    // Verificar que la entidad UserFilter también refleja la actualización
    UserFilterEntity updatedUserFilter =
        userFilterRepository.findById(userFilter.getId()).orElseThrow();
    assertEquals("Updated Pattern Name", updatedUserFilter.getResponsePattern().getName());
  }

  @Test
  public void givenUserFilterRepository_whenDeleted_thenOK() {
    // Crear y guardar un UserFilter
    UserFilterEntity userFilter = new UserFilterEntity();
    UserFilterEntity.UserFilterId userFilterId =
        new UserFilterEntity.UserFilterId(user.getUserId(), responsePattern.getResponsePatternId());
    userFilter.setId(userFilterId); // Asignar la clave compuesta
    userFilter.setUser(user);
    userFilter.setResponsePattern(responsePattern);
    userFilter = userFilterRepository.save(userFilter);

    // Eliminar el UserFilter
    userFilterRepository.delete(userFilter);

    // Verificar que se ha eliminado correctamente
    assertFalse(userFilterRepository.existsById(userFilter.getId()));
  }
}
