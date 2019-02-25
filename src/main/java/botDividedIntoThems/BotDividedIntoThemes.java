package botDividedIntoThems;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import org.telegram.telegrambots.api.objects.Message;

/**
 * класс реализующий функционал телеграм бота, который позволяет делить сообщения по темам
 * наследник {@link TelegramLongPollingBot}
 * @author Petrenko V. V.
 */
public class BotDividedIntoThemes extends TelegramLongPollingBot {

    /**
     * булевая переменная отвечающая за еденичный вызов метода {@link BotDividedIntoThemes#Help(Message)}
     */
    private  static boolean causeHelp = true;

    /**
     *
     * инициализирует телеграм апи
     */
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            botapi.registerBot(new BotDividedIntoThemes());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * возвращает имя бота
     * @return имя бота
     */
    public String getBotUsername() {
        return "BotDividedIntoThemes";
    }

    /**
     * функция вызывающаяяся при обновлении чата
     */
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(causeHelp){
            Help(message);
            causeHelp = false;
        }
        if(message != null && message.hasText()) {
            SwichMenu(message);
        }
    }

    /**
     * возвращает токен бота
     * @return токен бота
     */
    public String getBotToken() {
        return "730349966:AAELC6kCH6qzsc6iT2VHnkvpR5qA3CfIEmo";
    }

    /**
     * отвечает на сообщения
     * @param message сообщение на которое отвечает бот
     * @param text текст сообщения
     */
    private void sendMsg(Message message , String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        }
        catch (TelegramApiException tEx){
            tEx.printStackTrace();
        }
    }

    /**
     * пересылает сообщения
     * @param message сообщение которое пересылается
     * @param user пользователь которому тепересылается
     */
    private void forvardMessege(Message message , User user){
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setChatId(user.getId().toString());
        forwardMessage.setFromChatId(message.getChatId());
        forwardMessage.setMessageId(message.getMessageId());
        try {
            forwardMessage(forwardMessage);
        } catch (TelegramApiException tEx) {
            tEx.printStackTrace();
        }
    }

    /**
     * проверяет пользователя на то является ли он админом чата
     * @param message сообщения пользователя который проверятся
     * @return булевая переменная определябщая является пользователь админом
     */
    private boolean HasAccess(Message message){
        try {
            GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
            getChatAdministrators.setChatId(message.getChatId());
            for(ChatMember chatMember : getChatAdministrators(getChatAdministrators)){
                if(chatMember.getUser().getId().equals(message.getFrom().getId())){
                    return true;
                }
            }
        }
        catch (TelegramApiException ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * оправляет сообщения помеченное темой подписчикам {@link Theme#GetSubscribers()} на эту тему
     * @param message сообщение относящееся к какойто теме
     * @param themeName название темы
     */
    private void sendSubscribers(Message message , String themeName){
        for (Theme theme : Theme.GetListThemes()){
            for(User subscriber : theme.GetSubscribers()){
                if(themeName.equals(theme.GetTheme())&& theme.GetChatID().equals(message.getChatId().toString())) {
                    forvardMessege(message , subscriber);
                }
            }
        }


    }

    /**
     * позволяет пользователям (только админам) добавлять новую тему{@link Theme#}
     * если в чате не существует темы с таким названием
     * @param message сообщение от пользрвателя
     * @param themeName название новой темы
     */
    private void NewTheam(Message message , String themeName){
        if(!HasAccess(message)){
            sendMsg(message, "только администраторы могут добавлять темы");
            return;
        }
        if (themeName!=null) {
            if (!Theme.HasTheme(themeName ,  message.getChatId().toString())) {
                Theme.AddThem(themeName , message.getChatId().toString());
                sendMsg(message, "добавлена тема : " + themeName);
            } else {
                sendMsg(message, "тема с таким названием уже существует");
            }
        }
        else {
            sendMsg(message, "название темы должно указываться в скобках: <тема>");
        }
    }

    /**
     * позволяет пользователям (только админам) удалять существующую в чате тему {@link Theme#}
     * @param message сообщение от пользоватея
     * @param themeName название темы которую необходимо удалить
     */
    private void DeleteTheame(Message message , String themeName){
        if(!HasAccess(message)){
            sendMsg(message, "только администраторы могут удалять темы");
            return;
        }
        if(Theme.DeleteTheme(themeName , message.getChatId().toString())){
            sendMsg( message , "тема : "+themeName+" удалена");
        }
        else {
            sendMsg(message , "тема :"+themeName + "не найдена");
        }
        return;
    }

    /**
     * присылает в чат сообщение с перечнем существующих в чате тем
     * @param message сообщение от пользователя
     */
    private void TheamsList(Message message){
        String  themes = "список тем:";
        for(Theme theme : Theme.GetListThemes()){
            themes = themes +"\n"+ theme.GetTheme();
        }
        if (!themes.equals("список тем:"))
            sendMsg(message , themes);
        else sendMsg(message , "сейчас нету обсуждаемых тем");
    }

    /**
     * позволяет помечать сообщения , после чего они привязываются к определенной теме
     * @param message сообщения позьзователся вызвавшего эту функцию
     * @param themeName название темы
     */
    private void AnnotateMassege(Message message , String themeName){
        if (Theme.HasTheme(themeName ,  message.getChatId().toString())) {
            Theme.AddMassegeToThem(message, themeName , message.getChatId().toString());
            sendSubscribers(message , themeName);
        }
        else {
            sendMsg(message , "тема не найдена");
        }
    }

    /**
     *пересылает в личку сообщения относящиеся к о теме
     * @param message сообщения позьзователся вызвавшего эту функцию
     * @param themeName название темы
     */
    private void ViewTopic(Message message, String themeName){
        if (Theme.HasTheme(themeName ,  message.getChatId().toString())) {
            if(Theme.GetMessagesPerTheam(themeName , message.getChatId().toString())==null){
                sendMsg(message, "сообщений по теме : "+themeName+" не найдено");
                return;
            }
            for(Message mess: Theme.GetMessagesPerTheam(themeName , message.getChatId().toString())){
                forvardMessege(mess, message.getFrom());
            }
        }
        else {
            sendMsg(message, "тема : "+themeName+" не найдена");
        }
    }

    /**
     * добавляет нового подписчика
     * @param message сообщение от пользователя который подписывается на тему
     * @param themeName название темы
     */
    private void NewSubscriber(Message message , String themeName){
        if (!Theme.HasTheme(themeName ,  message.getChatId().toString())){
            sendMsg(message , "тема :"+themeName + " не найдена");
            return;
        }
        if(Theme.AddSubscriber(message.getFrom() , themeName , message.getChatId().toString())){
            sendMsg(message , "вы подписались на тему : "+themeName);
        }
        else {
            sendMsg(message , "вы уже подписаны на тему : "+themeName);
        }
    }

    /**
     * убирает пользователя из списка подписчмков на тему
     * @param message сообщение от пользвателя
     * @param themeName название темы
     */
    private void Unsubscribe(Message message, String themeName){
        if (!Theme.HasTheme(themeName ,  message.getChatId().toString())){
            sendMsg(message , "тема :"+themeName + " не найдена");
            return;
        }
        if(Theme.DeleteSubscriber(message.getFrom().getId().toString() , themeName , message.getChatId().toString())){
            sendMsg(message , "вы отписалисть от темы : "+themeName);
        }
        else {
            sendMsg(message , "вы и так не являетесь подпищиком темы : "+themeName);
        }
    }

    /**
     * присыает соощение с описние комант бота
     * @param message ообщение от пользвателя
     */
    private void Help(Message message){
        String help = "команды для управления ботом:\n " +
                "/новая тема <тема> - добавить новую тему (доступ имеют только адмнистраторы)\n" +
                "/список тем - просмотреть список тем в данном чате \n" +
                "/тема <тема> + сообщение - сообщение относится к указанной в скобках теме\n" +
                "/просмотреть тему <тема> - получишь в личку от бота все сообщения помеченные определенной темой\n" +
                "/удалить тему <тема> - удалить тему (доступ имеют только адмнистраторы)" +
                "/подписаться <тема> - все сообщения помеченные определенной темой будут перепралятся ботом в личку\n" +
                "/отписаться <тема> - отписатся от темы"+
                "/помощь - выводит данное сообщение";
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(help);
        try {
            sendMessage(sendMessage);
        }
        catch (TelegramApiException tEx){
            tEx.printStackTrace();
        }
    }

    /**
     * принимает решение как отреагировать на сообщение пользователя
     * @param message сообщение пользователя
     */
    private void SwichMenu(Message message){
        int selector =0;
        if(message.getText().contains("/новая тема")){
            selector =1 ;
        }
        if(message.getText().contains("/список тем")){
            selector =2;
        }
        if(message.getText().contains("/тема")){
            selector =3;
        }
        if(message.getText().contains("/просмотреть тему")){
            selector =4;
        }
        if(message.getText().contains("/удалить тему")){
            selector =5;
        }
        if(message.getText().contains("/подписаться")){
            selector =6;
        }
        if(message.getText().contains("/отписаться")){
            selector =7;
        }
        if(message.getText().contains("/помощь")){
            selector =9;
        }
        String themeName = Theme.themName(message.getText());
        switch (selector) {
            case 1:
                NewTheam(message , themeName);
                break;
            case 2:
                TheamsList(message );
                break;
            case 3:
                AnnotateMassege(message , themeName);
                break;
            case 4:
                ViewTopic(message , themeName);
                break;
            case 5:
                DeleteTheame(message , themeName);
                break;
            case 6:
               NewSubscriber(message , themeName);
                break;
            case 7:
                Unsubscribe(message , themeName);
                break;
            case 9:
                Help(message);
                break;
        }
    }
}