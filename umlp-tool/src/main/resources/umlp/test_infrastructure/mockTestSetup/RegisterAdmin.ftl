<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void registerAdmin(String userName)")}

${cdName}Manager.userBuilder()
                    .username(userName)
                    .password(userName)
                    .email(userName + "@rwth-aachen.de")
                    .role(dtcockpit.${cdName}Role.ADMIN)
                    .registrationDate(java.time.LocalDateTime.now())
                    .build();
