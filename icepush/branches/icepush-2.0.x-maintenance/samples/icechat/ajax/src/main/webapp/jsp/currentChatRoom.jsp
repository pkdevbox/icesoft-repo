<%--
 * Version: MPL 1.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2010 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 --%>
 
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>       
<div class="chatRoomView">
	<form id="chatRoomForm">
		<input type="hidden" id="roomName"/>
		<div class="chatRoomHeader">
			Chat Room '<span id="currentChatRoomName"></span>'
		</div>
		<div class="chatRoomContainer">
			<div class="chatRoomUsers">
				<div class="chatViewSubHeader">Who's Here?</div>
				<span id="currentChatRoomUsers"></span>
			</div>
			<div class="chatRoomMessages">
				<div class="chatViewSubHeader">Messages</div>
				<span id="currentChatRoomMessages"></span>
			</div>
		</div>
		<div class="clearer"></div>
		<div class="addNewMessage">
			New Message&nbsp;
			<input type="text" id="newChatRoomMessage" style="width:40%;" />
			<input type="button" onclick="sendNewChatRoomMessage();"
				value="Send" />
		</div>
	</form>
</div>
