/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  mnunez
 * Created: Feb 22, 2024
 */

drop database rsyncbot;
create database rsyncbot;
use rsyncbot;
create table credentials(id int not null auto_increment,
                         name varchar(255),
                         user varchar(255),
                         key_path varchar(255),
                         primary key (id));

create table servers(id int not null auto_increment,
                     name varchar(255),
                     host varchar(255),
                     primary key (id));

 create table jobs(id int not null auto_increment,
                  name varchar(255) not null,
                  schedule varchar(255) not null,
                  source_server_id int,
                  source_path varchar(255) not null,
                  destination_server_id int,
                  destination_path varchar(255) not null,
                  credential_id int,
                  primary key (id));
                  --foreign key (source_server_id) references servers (id),
                  --foreign key (source_credential_id) references credentials (id),
                  --foreign key (destination_server_id) references servers (id),
                  --foreign key (destination_credential_id) references credentials (id));

