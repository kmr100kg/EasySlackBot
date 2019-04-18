CREATE TABLE IF NOT EXISTS `message_summary`(
  `id` int(11) NOT NULL,
  `overview` varchar(255) NOT NULL DEFAULT '',
  `question` varchar(255) NOT NULL,
  `must_morpheme` varchar(255) NOT NULL,
  `answer` varchar(255) NOT NULL,
  `threshold` int(11) NOT NULL,
  `priority` int(11) NOT NULL,
  PRIMARY KEY (`id`)
  );

INSERT INTO `message_summary` VALUES(1, '給料日の回答' ,'給料,日,い,つ,？' ,'給料,日' ,'オメーの給料ねーから！' ,2 ,1);
INSERT INTO `message_summary` VALUES(2, 'お勧めアニメの回答' ,'オススメ,勧め,アニメ,教え,？' ,'アニメ' ,'GHOST IN THE SHELL' ,3 ,1);
