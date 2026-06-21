package com.breads.minds.config;

import com.breads.minds.entity.*;
import com.breads.minds.entity.enums.*;
import com.breads.minds.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Seeds the database with initial reference data and default users
 * on first startup (dev profile only when data is absent).
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("dev")
    CommandLineRunner seedData(
            AreaRepository areaRepo,
            DistrictRepository districtRepo,
            SchoolRepository schoolRepo,
            UserRepository userRepo,
            HealthProgramRepository healthProgramRepo,
            NoticeRepository noticeRepo) {

        return args -> {
            // When using Option-2 SQL scripts, areas/districts/schools are already inserted.
            // We only skip if the default admin user already exists.
            if (userRepo.existsByUsername("superadmin")) {
                log.info("Users already seeded – skipping user initialization.");
                return;
            }

            log.info("Seeding initial data...");

            // ── Areas (skip if already inserted by seed_data.sql) ──────
            Area southKarnataka = areaRepo.findByNameIgnoreCase("South Karnataka")
                    .orElseGet(() -> areaRepo.save(Area.builder().name("South Karnataka").build()));
            Area northKarnataka = areaRepo.findByNameIgnoreCase("North Karnataka")
                    .orElseGet(() -> areaRepo.save(Area.builder().name("North Karnataka").build()));

            // ── Districts (skip if already inserted) ──────────────────
            District bangalore = districtRepo.findByNameIgnoreCase("Bangalore")
                    .orElseGet(() -> districtRepo.save(District.builder().name("Bangalore").area(southKarnataka).build()));
            District chitradurga = districtRepo.findByNameIgnoreCase("Chitradurga")
                    .orElseGet(() -> districtRepo.save(District.builder().name("Chitradurga").area(southKarnataka).build()));
            District bidar = districtRepo.findByNameIgnoreCase("Bidar")
                    .orElseGet(() -> districtRepo.save(District.builder().name("Bidar").area(northKarnataka).build()));
            District yadgir = districtRepo.findByNameIgnoreCase("Yadgir")
                    .orElseGet(() -> districtRepo.save(District.builder().name("Yadgir").area(northKarnataka).build()));

            // ── Schools (skip if district already has schools) ────────
            if (schoolRepo.countByDistrictId(bangalore.getId()) == 0) {
                schoolRepo.saveAll(List.of(
                    School.builder().name("St. Joseph High School").district(bangalore).isActive(true).build(),
                    School.builder().name("Government Primary School - Ulsoor").district(bangalore).isActive(true).build()
                ));
            }
            if (schoolRepo.countByDistrictId(bidar.getId()) == 0)
                schoolRepo.save(School.builder().name("Vidhya Vardhaka School").district(bidar).isActive(true).build());
            if (schoolRepo.countByDistrictId(chitradurga.getId()) == 0)
                schoolRepo.save(School.builder().name("Chitradurga Government High School").district(chitradurga).isActive(true).build());
            if (schoolRepo.countByDistrictId(yadgir.getId()) == 0)
                schoolRepo.save(School.builder().name("Yadgir Model School").district(yadgir).isActive(true).build());

            // ── Users ─────────────────────────────────────────────────
            userRepo.saveAll(List.of(
                User.builder()
                    .username("superadmin")
                    .password(passwordEncoder.encode("breads_master"))
                    .name("BREADS Director")
                    .email("director@breads.org")
                    .role(UserRole.SUPER_ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build(),

                User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("breads_hq"))
                    .name("Bangalore HQ Admin")
                    .email("admin@breads.org")
                    .mobile("+91 98765 43210")
                    .role(UserRole.BREADS_COORDINATOR)
                    .status(UserStatus.ACTIVE)
                    .build(),

                User.builder()
                    .username("dc_bangalore")
                    .password(passwordEncoder.encode("minds_blr"))
                    .name("Bangalore Coordinator")
                    .email("blr.coord@breads.org")
                    .mobile("+91 98888 77777")
                    .role(UserRole.DISTRICT_COORDINATOR)
                    .status(UserStatus.ACTIVE)
                    .district(bangalore)
                    .build(),

                User.builder()
                    .username("dc_bidar")
                    .password(passwordEncoder.encode("minds_bdr"))
                    .name("Bidar Coordinator")
                    .email("bidar.coord@breads.org")
                    .mobile("+91 91111 22222")
                    .role(UserRole.DISTRICT_COORDINATOR)
                    .status(UserStatus.ACTIVE)
                    .district(bidar)
                    .build(),

                User.builder()
                    .username("dc_chitradurga")
                    .password(passwordEncoder.encode("minds_ctg"))
                    .name("Chitradurga Coordinator")
                    .email("ctg.coord@breads.org")
                    .role(UserRole.DISTRICT_COORDINATOR)
                    .status(UserStatus.ACTIVE)
                    .district(chitradurga)
                    .build(),

                User.builder()
                    .username("dc_yadgir")
                    .password(passwordEncoder.encode("minds_ydg"))
                    .name("Yadgir Coordinator")
                    .email("ydg.coord@breads.org")
                    .role(UserRole.DISTRICT_COORDINATOR)
                    .status(UserStatus.ACTIVE)
                    .district(yadgir)
                    .build()
            ));

            // ── Health Programs (skip if already present) ─────────────
            if (healthProgramRepo.count() == 0) healthProgramRepo.saveAll(List.of(
                HealthProgram.builder()
                    .name("Emotional Resilience Workshop")
                    .isActive(true)
                    .dedicatedFor("Children")
                    .objective("Build coping mechanisms for academic stress.")
                    .build(),
                HealthProgram.builder()
                    .name("Positive Parenting Seminar")
                    .isActive(true)
                    .dedicatedFor("Parents")
                    .objective("Enhance family communication and child mental health support.")
                    .build(),
                HealthProgram.builder()
                    .name("Teacher Well-Being Program")
                    .isActive(true)
                    .dedicatedFor("Teachers")
                    .objective("Support teacher mental health for better classroom outcomes.")
                    .build(),
                HealthProgram.builder()
                    .name("Community Mental Health Awareness")
                    .isActive(true)
                    .dedicatedFor("Community Leaders/Volunteers")
                    .objective("Create community-level mental health champions.")
                    .build()
            ));

            // ── Welcome Notice (skip if already present) ──────────────
            if (noticeRepo.count() == 0) noticeRepo.save(Notice.builder()
                .title("Welcome to BREADS MINDS Portal")
                .message("The system has been set up successfully. Please review the reporting guidelines and submit monthly reports by the 5th of each month.")
                .priority(NoticePriority.HIGH)
                .targetRoles(List.of(UserRole.DISTRICT_COORDINATOR, UserRole.BREADS_COORDINATOR, UserRole.SUPER_ADMIN))
                .isActive(true)
                .createdBy("System")
                .build());

            log.info("Data seeding complete (users only if SQL scripts were run first): 2 areas, 4 districts, 5 schools, 6 users, 4 health programs, 1 notice.");
        };
    }
}
