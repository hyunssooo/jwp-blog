package techcourse.myblog.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import techcourse.myblog.application.dto.LoginDto;
import techcourse.myblog.application.dto.UserDto;
import techcourse.myblog.application.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public RedirectView createUser(@Valid UserDto user) {
        RedirectView redirectView = new RedirectView("/login");
        userService.save(user);
        return redirectView;
    }

    @GetMapping("/users")
    public ModelAndView readUsers() {
        ModelAndView modelAndView = new ModelAndView("user-list");
        modelAndView.addObject("users", userService.findAll());
        return modelAndView;
    }

    @PostMapping("/login")
    public RedirectView login(HttpSession httpSession, LoginDto loginDto) {
        userService.login(loginDto);

        RedirectView redirectView = new RedirectView("/");
        httpSession.setAttribute("email", loginDto.getEmail());
        httpSession.setMaxInactiveInterval(600);
        return redirectView;
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpSession httpSession) {
        httpSession.invalidate();

        return new RedirectView("/");
    }

    @GetMapping("/mypage")
    public ModelAndView readMyPage(HttpSession httpSession) {
        ModelAndView modelAndView = new ModelAndView();
        String email = (String) httpSession.getAttribute("email");
        System.out.println(email);
        modelAndView.setViewName("mypage");
        modelAndView.addObject("user", userService.findByEmail(email));

        return modelAndView;
    }

    @GetMapping("/mypage/edit")
    public ModelAndView readMyPageEdit(HttpSession httpSession) {
        ModelAndView modelAndView = new ModelAndView();
        String email = (String) httpSession.getAttribute("email");
        modelAndView.setViewName("mypage-edit");
        modelAndView.addObject("user", userService.findByEmail(email));
        return modelAndView;
    }

    @PutMapping("/mypage/edit")
    public RedirectView updateUser(HttpSession httpSession, @Valid UserDto user) {
        RedirectView redirectView = new RedirectView();
        String email = (String) httpSession.getAttribute("email");

        userService.modify(user, email);

        redirectView.setUrl("/mypage");
        return redirectView;
    }

    @DeleteMapping("/users")
    public RedirectView deleteUser(HttpSession httpSession, @Valid UserDto user) {
        RedirectView redirectView = new RedirectView("/");
        String email = (String) httpSession.getAttribute("email");

        userService.removeById(user, email);
        httpSession.invalidate();

        return redirectView;
    }
}
