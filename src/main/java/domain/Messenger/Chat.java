package domain.Messenger;

import domain.Medicalcase.Medicalcase;
import domain.User.User;
import domain.common.BaseEntity;
import domain.common.Media;
import domain.common.TextContent;
import repository.ChatRepository;
import repository.MedicalcaseRepository;

import java.time.Instant;
import java.util.*;

import static foundation.Assert.*;

public class Chat extends BaseEntity {

    // can be null if groupchat false - not null otherwise, not blank, max 64 characters
    private String name;
    private boolean groupChat;
    // not null, max 512 members, not empty
    private Set<UUID> members;
    // not null
    private List<Message> history;

    public Chat(String name, Set<UUID> members, boolean groupChat) {
        this.groupChat = groupChat;
        setName(name);
        setMembers(members);
        history = new ArrayList<>();
        save();
    }

    public void setName(String name) {
        if (groupChat) {
            isNotNull(name, "name");
            hasMaxLength(name, 513, "name");
            isNotBlank(name, "name");
            this.name = name;
            save();
        }
    }

    public void setMembers(Set<UUID> members) {
        isNotNull(members, "members");
        hasMaxSize(members,513, "members");
        this.members = new HashSet<>(members);
    }

    public void addMember(User user) {
        isNotNull(user, "user");
        hasMaxSize(members, 513, "members");

        // if the chat is a direct chat (non-groupchat), create a new groupchat
        if (!groupChat) {
            // todo somehow get name from user based on id and also change the statement to output this: memberName1, memberName2, etc.
            String groupName = members.stream().map(UUID::toString).toString() + user.getProfile().getName();
            Chat chat = new Chat(groupName, new HashSet<UUID>(members), true);
            user.getChats().add(chat);
            // todo other members also have to add the chat (again get user from uuid)
        }
        if (members.contains(user.getId()))
            throw new MessengerException(STR."addMember(): user is already a member of this chat");
        members.add(user.getId());
        user.getChats().add(this);
        save();
    }

    public void removeMember(User user) {
        if (!groupChat)
            throw new MessengerException(STR."removeMember(): can not remove a member from a direct chat");
        isNotNull(user, "user");
        hasMaxSize(members, 513, "members");

        if (!(members.contains(user.getId())))
            throw new MessengerException(STR."removeMember(): user is not a member of this chat");
        // you can not create a chat with only yourself, but you can remove a member from a groupchat with only 2 members
        members.remove(user.getId());
        user.getChats().remove(this);
        save();
    }

    public void addToHistory(Message message){
        isNotNull(message,"message");
        history.add(message);
    }

    public void sendMessage(User user, Chat chat, TextContent content, List<Media> attachments){
        isNotNull(chat, "chat");
        isNotNull(content, "content");
        isNotNull(user, "user");

        if(!(members.contains(user.getId())))
            throw new MessengerException("sendMessage(): User is not a member of this chat");
        chat.addToHistory(new Message(user, Instant.now(), content, attachments, Status.SENT));
        save();
    }

    public String getName() {
        return name;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public List<Message> getHistory() {
        return history;
    }

    public boolean isGroupChat() {
        return groupChat;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        history.forEach(sb::append);
        return sb.toString();
    }

    @Override
    public void save() {
        ChatRepository.save(this);
    }

 //Test for sending Messages between 2 Users (it actually works) pls dont delete i need this code
    public static void main(String[] args) {
//        User homer = new User("homer@simpson.com", "password", "Homer Simpson", "Rh.D.", "United Kingdom");
//        User bart = new User("bart@simpson.com", "password", "Bart Simpson", "Ph.D.", "United States");
//        User lisa = new User("lisa@simpson.com", "password", "Lisa Simpson", "Ph.D.", "United States");
//        User test = new User("test@simpson.com", "password", "test Simpson", "Ph.D.", "United States");
//        homer.addFriend(bart);
//        bart.acceptFriendRequest(homer);
//
//        lisa.createGroupChat("theCoolOnes", Set.of(bart.getId(), homer.getId()));
//
//        if (homer.getDirectChat(bart.getId()).isPresent()) {
//            Chat chat = homer.getDirectChat(bart.getId()).get();
//            homer.sendMessage(chat, new TextContent("Das ist Homer Message test test"), List.of(new Media("hallo.txt", "sdhjgvbfd", 100), new Media("test.txt", "sdhjgvbfd", 100)));
//            bart.sendMessage(chat, new TextContent("Das ist Bart Message hallo 123 Test"), List.of(new Media("hallo.txt", "sdhjgvbfd", 100)));
//            homer.viewChat(chat);
//        }
//
//        Chat chat = ChatRepository.findByName("theCoolOnes").get(0);
//        homer.sendMessage(chat, new TextContent("Das ist Homer Message hallo 123 Test"), null);
//        lisa.viewChat(chat);
////        homer.viewChat(chat);
////        bart.viewChat(chat);
        User homer = new User("homer@simpson.com", "password", "Homer Simpson", "Rh.D.", "United Kingdom");
        User bart = new User("bart@simpson.com", "password", "Bart Simpson", "Ph.D.", "United States");
//        homer.addFriend(bart);
//        bart.acceptFriendRequest(homer);
//        homer.createGroupChat("homer and bart", Set.of(bart.getId()));
//
//        Optional<Chat> chat = ChatRepository.findByName("homer and bart").stream().findFirst();
//        if (chat.isPresent()) {
//
//            homer.sendMessage(chat.get(), new TextContent("This is a message of Homer"), null);
//            homer.viewChat(chat.get());
//        }
        homer.createMedicalcase("Test");
        Medicalcase medicalcase = MedicalcaseRepository.findAll().stream().findFirst().get();
        medicalcase.addVotingOption("aids");
        medicalcase.addVotingOption("cancer");
        medicalcase.addVotingOption("idk");
        medicalcase.publish();
        medicalcase.addMember(bart);
        medicalcase.castVote(bart, "aids", 90);
        medicalcase.castVote(bart, "cancer", 10);
        medicalcase.viewVotes();
        bart.sendMessage(medicalcase.getChat(), "bart message in medicalcase", null);
        bart.sendMessage(medicalcase.getChat(), "bart message in medicalcase", null);
        medicalcase.viewChat();
    }
}
