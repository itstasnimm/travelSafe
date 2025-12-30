package com.example.womensafety.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.womensafety.entity.Journey;
import com.example.womensafety.entity.Users;
import com.example.womensafety.repository.JourneyRepository;
import com.example.womensafety.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController{

    @Autowired
    private UserRepository userRepository;
    private JourneyRepository journeyRepository;

    @GetMapping("/")
    public String enterUser(){
        return "userEnter";
    }

    @PostMapping("/entryNoEntry")
    public String entry(@RequestParam String uname,@RequestParam String upassword,@RequestParam(required=false) String urb,HttpSession session,Model model){
        boolean newUser= (urb!=null);
        if(newUser){
            Users u1 = userRepository.findByUname(uname);
            if(u1==null){
                Users u2= new Users();
                u2.setUname(uname);
                u2.setUpassword(upassword);
                Users u3 = userRepository.save(u2);
                session.setAttribute("uid", u3.getUid());
            }else{
                model.addAttribute("error", "Username already exists");
                return "userEnter";
            }
        }else{
            Users u3= userRepository.findByUnameAndUpassword(uname,upassword);
            if(u3==null){
                model.addAttribute("error", "Invalid Login attempt");
                return "userEnter";
            }
            session.setAttribute("uid", u3.getUid());
        }

        return "redirect:/chooseJourney";
    }

    @GetMapping("/chooseJourney")
    public String chooseJourney(HttpSession session, Model model){
        if(session.getAttribute("uid")==null){
            model.addAttribute("error", "Only logged in users can create journey");
            return "userEnter";
        }
        return "chooseJourney";
    }

    // public List<Journey> getExisting(@RequestParam("uid") Long u){
    @GetMapping("/existing")
    public List<Journey> getExisting(HttpSession session){
        Long uid= (Long) session.getAttribute("uid");
        Optional<Users> u1= userRepository.findById(uid);
        Users u2 = u1.orElse(null);
        return u2==null?new ArrayList<>():u2.getJourneys();
    }

     @GetMapping("/existingJourney")
    public String updateExisting(@RequestParam("jid") Long j,Model model){
        Optional<Journey> j1= journeyRepository.findById(j);
        Journey j2 = j1.orElse(null);
        j2.setTrackId(UUID.randomUUID().toString());
        Journey updated = journeyRepository.save(j2);
        model.addAttribute("journey", updated);
        model.addAttribute("trackLink", "/journey/track/" + updated.getTrackId());
        return "journey/livetracking";
    }

}