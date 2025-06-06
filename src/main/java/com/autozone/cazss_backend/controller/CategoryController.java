package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.CategoryDTO;
import com.autozone.cazss_backend.DTO.UserCategoryDTO;
import com.autozone.cazss_backend.service.CategoryService;
import com.autozone.cazss_backend.service.UserCategoryService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/categories")
public class CategoryController {
  @Autowired CategoryService categoryService;
  @Autowired UserCategoryService userCategoryService;

  /**
   * GET /categories If user is an admin, get all categories. Otherwise, fetch user specific
   * categories
   *
   * @return List&ltCategoryDTO&gt which contains category ids, names and colors
   */
  // @GetMapping("")
  // public ResponseEntity<List<CategoryDTO>> getCategories() {
  //   return new ResponseEntity<>(new ArrayList<CategoryDTO>(), HttpStatus.OK);
  // }

  /**
   * POST /categories If the user is an admin, create a category with the specified characteristics
   *
   * @param categoryDTO Data to give the new category
   * @return CategoryDTO of the specified category with the newly provided name and color
   */
  @PostMapping("")
  public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
    return new ResponseEntity<>(new CategoryDTO(), HttpStatus.CREATED);
  }

  /**
   * PUT /categories/{categoryId} If the user is an admin, update a category with the specified
   * characteristics
   *
   * @param categoryId ID of the category to update
   * @param categoryDTO Data to update for the specified category
   * @return CategoryDTO of the specified category with the newly provided name and/or color
   */
  @PutMapping("/{categoryId}")
  public ResponseEntity<CategoryDTO> updateCategory(
      @PathVariable Integer categoryId, @RequestBody CategoryDTO categoryDTO) {
    return new ResponseEntity<>(new CategoryDTO(), HttpStatus.OK);
  }

  /**
   * DELETE /categories/{categoryId} If the user is an admin, delete the specified category
   *
   * @param categoryId ID of the category to delete
   * @return String saying the deletion went through
   */
  @DeleteMapping("/{categoryId}")
  public ResponseEntity<String> deleteCategory(
      @RequestHeader Integer userId, @PathVariable Integer categoryId) {
    return new ResponseEntity<>(categoryService.deleteCategory(userId, categoryId), HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<CategoryDTO>> getCategories(@RequestHeader Integer userId) {
    List<CategoryDTO> categories = categoryService.getCategoriesByUserId(userId);
    return new ResponseEntity<>(categories, HttpStatus.OK);
  }

  /**
   * GET /categories/{categoryId}/user-categories If the user is an admin, fetch all users with
   * access to a category
   *
   * @param categoryId ID of the category to get a list of user access from
   * @return List&ltUserCategoryDTO&gt with the category and user ID
   */
  @GetMapping("/{categoryId}/user-categories")
  public ResponseEntity<List<UserCategoryDTO>> getUsersWithAccessToCategory(
      @PathVariable Integer categoryId) {
    return new ResponseEntity<>(new ArrayList<UserCategoryDTO>(), HttpStatus.OK);
  }

  /**
   * POST /categories/{categoryId}/user-categories If the user is an admin, add access to a category
   * for a specified list of users
   *
   * @param categoryId ID of the category to give access to for a list of users
   * @param usersToAdd List of user IDs to add
   * @return List&ltUserCategoryDTO&gt with the added users fetched from the database
   */
  @PostMapping("/{categoryId}/user-categories")
  public ResponseEntity<List<UserCategoryDTO>> addPermissionToAccessCategoryToUsers(
      @PathVariable Integer categoryId, @RequestBody List<Integer> usersToAdd) {
    return new ResponseEntity<>(new ArrayList<UserCategoryDTO>(), HttpStatus.CREATED);
  }

  /**
   * DELETE /categories/{categoryId}/user-categories If the user is an admin, delete access to a
   * category for a specified list of users
   *
   * @param categoryId ID of the category to delete access to for a list of users
   * @param usersToDelete List of user IDs to delete
   * @return String saying the deletion went through
   */
  @DeleteMapping("/{categoryId}/user-categories")
  public ResponseEntity<String> deleteAccessToCategoryForUsers(
      @PathVariable Integer categoryId, @RequestBody List<Integer> usersToDelete) {
    return new ResponseEntity<>(
        "Access to category " + categoryId + " deleted for users " + usersToDelete.toString(),
        HttpStatus.OK);
  }
}
