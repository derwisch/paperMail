package com.github.derwisch.paperMail;

import org.bukkit.permissions.Permission;

public class Permissions {
	public static final Permission CREATE_CHEST_PERM = new Permission(Strings.CREATE_CHEST_STR);
	public static final Permission SEND_ITEM_PERM = new Permission(Strings.SEND_ITEM_STR);
	public static final Permission SEND_TEXT_PERM = new Permission(Strings.SEND_TEXT_STR);
	
	public class Strings {
		public static final String CREATE_CHEST_STR = "papermail.createchest";
		public static final String SEND_ITEM_STR = "papermail.send.item";
		public static final String SEND_TEXT_STR = "papermail.send.text";		
	}
}
