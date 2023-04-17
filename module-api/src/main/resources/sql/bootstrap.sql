

LOCK TABLES `authority` WRITE;
/*!40000 ALTER TABLE `authority` DISABLE KEYS */;
INSERT INTO `authority` VALUES (1,'2023-03-22 23:43:58.189251','2023-03-22 23:43:58.189251','system','system','ff55c75c-94fa-46a7-9c89-c67c5f60d8d2','user.create'),(2,'2023-03-22 23:43:58.259936','2023-03-22 23:43:58.259936','system','system','b83579ef-590c-41b3-ba25-726f02fe21c9','user.update'),(3,'2023-03-22 23:43:58.262129','2023-03-22 23:43:58.262129','system','system','1d3d0ad2-be7f-4645-86d3-b63f2742ba40','user.read'),(4,'2023-03-22 23:43:58.264111','2023-03-22 23:43:58.264111','system','system','f5c7116c-6aa1-439f-8480-8f21143414b0','user.delete'),(5,'2023-03-22 23:43:58.265751','2023-03-22 23:43:58.265751','system','system','d04f9a83-76df-4eb5-9231-efeac3d1bd62','opinion.create'),(6,'2023-03-22 23:43:58.267424','2023-03-22 23:43:58.267424','system','system','164200fa-9c80-4fe1-8b58-95ffd673d08b','opinion.update'),(7,'2023-03-22 23:43:58.268920','2023-03-22 23:43:58.268920','system','system','32995059-93a6-4e9f-95b4-63149cea7278','opinion.read'),(8,'2023-03-22 23:43:58.270591','2023-03-22 23:43:58.270591','system','system','261160d1-382c-40bc-af02-28c39fd99690','opinion.delete'),(9,'2023-03-22 23:43:58.272250','2023-03-22 23:43:58.272250','system','system','bef0a3f6-9819-4b1e-b0ab-3f8851020548','article.create'),(10,'2023-03-22 23:43:58.273807','2023-03-22 23:43:58.273807','system','system','e28c6c5b-f1de-42f2-b164-3f5015e59f90','article.update'),(11,'2023-03-22 23:43:58.275648','2023-03-22 23:43:58.275648','system','system','bde7732a-d0fb-420b-8dc3-33f015ad4be7','article.read'),(12,'2023-03-22 23:43:58.278030','2023-03-22 23:43:58.278030','system','system','e6381034-785a-49b4-8c53-b9fc4ddfe818','article.delete'),(13,'2023-03-22 23:43:58.281876','2023-03-22 23:43:58.281876','system','system','656de476-bb9b-4a49-bbde-95962a90ebf3','match.create'),(14,'2023-03-22 23:43:58.283586','2023-03-22 23:43:58.283586','system','system','1e2eecf1-d85f-4194-be7c-9c711149268b','match.update'),(15,'2023-03-22 23:43:58.285159','2023-03-22 23:43:58.285159','system','system','1cbf50ec-0f17-4cb9-8285-c6c87020c164','match.read'),(16,'2023-03-22 23:43:58.287081','2023-03-22 23:43:58.287081','system','system','c87bbd98-148a-487d-8f36-e4a739b0e698','match.delete'),(17,'2023-03-22 23:43:58.288668','2023-03-22 23:43:58.288668','system','system','343d9734-3df0-4d67-9e4e-cf10fa5d6a51','activity.create'),(18,'2023-03-22 23:43:58.290067','2023-03-22 23:43:58.290067','system','system','abc0997d-0bed-471a-8de3-21e1ac8dcc9d','activity.update'),(19,'2023-03-22 23:43:58.291456','2023-03-22 23:43:58.291456','system','system','a647103b-a07b-41cf-9a1f-70421c1c39b3','activity.read'),(20,'2023-03-22 23:43:58.292850','2023-03-22 23:43:58.292850','system','system','fdaf7b6e-62dd-4c5e-9e80-875b7f928e97','activity.delete'),(21,'2023-03-22 23:43:58.294432','2023-03-22 23:43:58.294432','system','system','1fc2eee3-06c1-4bf1-bb85-48678c997225','random-match.create'),(22,'2023-03-22 23:43:58.295809','2023-03-22 23:43:58.295809','system','system','6418c814-2a71-4952-a2d4-7e51e49c3ee1','random-match.update'),(23,'2023-03-22 23:43:58.297212','2023-03-22 23:43:58.297212','system','system','e28aa48c-b9e0-4776-b4e6-6df7329a59dc','random-match.read'),(24,'2023-03-22 23:43:58.298573','2023-03-22 23:43:58.298573','system','system','0f58a5db-d6d9-4461-8990-097dbfd2c012','random-match.delete'),(25,'2023-03-22 23:43:58.300103','2023-03-22 23:43:58.300103','system','system','4cf7859a-e444-4da1-9171-f676c2d8945c','alarm.create'),(26,'2023-03-22 23:43:58.301622','2023-03-22 23:43:58.301622','system','system','8119d6fd-0797-419f-8350-82b740428d04','alarm.update'),(27,'2023-03-22 23:43:58.303105','2023-03-22 23:43:58.303105','system','system','979091e6-09d2-420b-99a7-d96bd19c206d','alarm.read'),(28,'2023-03-22 23:43:58.304473','2023-03-22 23:43:58.304473','system','system','99e9c887-6e7e-417e-9579-eeadec8f1279','alarm.delete');
/*!40000 ALTER TABLE `authority` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `match_condition` WRITE;
/*!40000 ALTER TABLE `match_condition` DISABLE KEYS */;
INSERT INTO `match_condition` VALUES (1,'2023-03-22 23:43:58.530512','2023-03-22 23:43:58.530512','system','system','WayOfEating','DELIVERY'),(2,'2023-03-22 23:43:58.533598','2023-03-22 23:43:58.533598','system','system','WayOfEating','EATOUT'),(3,'2023-03-22 23:43:58.535696','2023-03-22 23:43:58.535696','system','system','WayOfEating','TAKEOUT'),(4,'2023-03-22 23:43:58.536855','2023-03-22 23:43:58.536855','system','system','Place','SEOCHO'),(5,'2023-03-22 23:43:58.537948','2023-03-22 23:43:58.537948','system','system','Place','GAEPO'),(6,'2023-03-22 23:43:58.539133','2023-03-22 23:43:58.539133','system','system','Place','OUT_OF_CLUSTER'),(7,'2023-03-22 23:43:58.540486','2023-03-22 23:43:58.540486','system','system','TimeOfEating','BREAKFAST'),(8,'2023-03-22 23:43:58.541720','2023-03-22 23:43:58.541720','system','system','TimeOfEating','LUNCH'),(9,'2023-03-22 23:43:58.543357','2023-03-22 23:43:58.543357','system','system','TimeOfEating','DUNCH'),(10,'2023-03-22 23:43:58.545158','2023-03-22 23:43:58.545158','system','system','TimeOfEating','DINNER'),(11,'2023-03-22 23:43:58.546860','2023-03-22 23:43:58.546860','system','system','TimeOfEating','MIDNIGHT'),(12,'2023-03-22 23:43:58.548207','2023-03-22 23:43:58.548207','system','system','TypeOfStudy','INNER_CIRCLE'),(13,'2023-03-22 23:43:58.549403','2023-03-22 23:43:58.549403','system','system','TypeOfStudy','NOT_INNER_CIRCLE');
/*!40000 ALTER TABLE `match_condition` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
INSERT INTO `member` VALUES (1,'2023-03-22 23:43:58.471408','2023-03-22 23:43:58.471408','system','system','24f4156c-648f-42d9-9709-931804ae50ca','sorkim'),(2,'2023-03-22 23:43:58.509326','2023-03-22 23:43:58.509326','system','system','155b124a-9185-4ccb-9a93-0258894fd8fb','hyenam'),(3,'2023-03-22 23:43:58.522291','2023-03-22 23:43:58.522291','system','system','88d8ec5e-bc0a-43f3-8cc1-32b78952117c','takim');
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'2023-03-22 23:43:58.308788','2023-03-22 23:43:58.308788','system','system','dfd3039b-7796-47e0-90d9-e0dfb492c97a','ROLE_ADMIN'),(2,'2023-03-22 23:43:58.320130','2023-03-22 23:43:58.320130','system','system','7fe2a791-55fe-4dc1-b5f1-668e9f498365','ROLE_USER');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `role_authority` WRITE;
/*!40000 ALTER TABLE `role_authority` DISABLE KEYS */;
INSERT INTO `role_authority` VALUES (1,1),(2,1),(1,2),(2,2),(1,3),(2,3),(1,4),(2,4),(1,5),(2,5),(1,6),(2,6),(1,7),(2,7),(1,8),(2,8),(1,9),(2,9),(1,10),(2,10),(1,11),(2,11),(1,12),(2,12),(1,13),(2,13),(1,14),(2,14),(1,15),(2,15),(1,16),(2,16),(1,17),(2,17),(1,18),(2,18),(1,19),(2,19),(1,20),(2,20),(1,21),(2,21),(1,22),(2,22),(1,23),(2,23),(1,24),(2,24),(1,25),(2,25),(1,26),(2,26),(1,27),(2,27),(1,28),(2,28);
/*!40000 ALTER TABLE `role_authority` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2023-03-22 23:43:58.486047','2023-03-22 23:43:58.486047','system','system','57bdfdc9-c20d-4c40-8ce8-d939c72b76f5','sorkim@student.42seoul.kr','https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/sorkim.jpg',_binary '',_binary '',0,'sorkim','d9e552b9-f6de-4ce6-a821-e2a869d117da',NULL,'sorkim',1),(2,'2023-03-22 23:43:58.513090','2023-03-22 23:43:58.513090','system','system','ba901720-3c14-4af1-aa0a-75d52c63f282','hyenam@student.42seoul.kr','https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/hyenam.jpg',_binary '',_binary '',0,'hyenam','f30b575c-e5f1-46b2-91b8-b14ecc83228e',NULL,'hyenam',2),(3,'2023-03-22 23:43:58.524475','2023-03-22 23:43:58.524475','system','system','15d111d8-e146-4412-946b-99defcdeef26','takim@student.42seoul.kr','https://cdn.intra.42.fr/users/0f260cc3e59777f0f5ba926f19cc1ec9/takim.jpg',_binary '',_binary '',0,'takim','cc836e0f-ec3b-4cf0-af48-89078e94b0db',NULL,'takim',3);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'2023-03-22 23:43:58.492442','2023-03-22 23:43:58.492442','system','system',2,1),(2,'2023-03-22 23:43:58.515406','2023-03-22 23:43:58.515406','system','system',2,2),(3,'2023-03-22 23:43:58.525178','2023-03-22 23:43:58.525178','system','system',2,3);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
