package com.sshih.ExpenseTrackerAPI.User;

import org.hibernate.annotations.DialectOverride.OverridesAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sshih.ExpenseTrackerAPI.Expense.Expense;
import com.sshih.ExpenseTrackerAPI.Expense.ExpenseRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.management.RuntimeErrorException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Expense addExpenseToUser(Long userId, Expense expense) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        expense.setUser(user);
        expenseRepository.save(expense);
        user.addExpense(expense);
        userRepository.save(user); // Due to CascadeType.ALL, expense will be saved

        return expense;
    }

    public User deleteExpenseFromUser(Long userId, Long expenseId) {
        Optional<Expense> toDelete = expenseRepository.findById(expenseId);
        if (toDelete.isPresent()) {
            expenseRepository.delete(expenseRepository.findById(expenseId).get());
            return findById(userId);
        }
        throw new NoSuchElementException("no exception of id: " + expenseId);
    }

    public List<Expense> getUserExpenses(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            return userRepository.findById(userId).get().getExpenses();
        }
        throw new RuntimeException("can't find user");
    }

    public User findById(Long id) {
        return userRepository.findById(id)
        .orElse(null);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
        .orElse(null);    
    }

    // Other methods (updateUser, deleteUser, etc.)
}