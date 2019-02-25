package botDividedIntoThems;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

import java.util.ArrayList;
import java.util.List;

/**
 * класс Тема
 * предлятавляет собой тему для обсуждения в чате телеграмма
 * @author Petrenko V. V.
 */
 class Theme {
    /**
     * поле отвечающее за название темы
     */
    private String theme;
    /**
     * список сообщений относящихся к определенной теме
     */
    private ArrayList<Message> massages;
    /**
     * список всех тем из всех чатов
     */
    private static ArrayList<Theme> Themes = new ArrayList<Theme>();
    /**
     * id чата в которому привязана тема
     * @see Theme#theme
     */
    private String chatId;
    /**
     * список подпсчиков на тему с названием {@link Theme#theme} и id часа {@link Theme#chatId}
     */
    private List<User> subscribers;

    /**
     * конструктор класса {@link Theme}
     * @param themeName название темы
     * @param chatId id чата темы
     */
    private Theme(String themeName , String chatId){
        this.theme = themeName;
        this.chatId = chatId;
        this.massages = new ArrayList<Message>();
        this.subscribers = new ArrayList<User>();
    }

    /**
     * @return названине темы
     */
    String GetTheme(){
        return this.theme;
    }

    /**
     * @return список всех тем во всех чатах
     */
    static ArrayList<Theme> GetListThemes (){
        return Themes;
    }

    /**
     * @return все сообщения по определенной теме
     */
    private ArrayList<Message> GetMassages(){ return this.massages; }

    /**
     * @return список подписчиков на определенныю тему
     */
    List<User> GetSubscribers(){
        return this.subscribers;
    }

    /**
     * @return id чата темы
     */
    String GetChatID(){  return  this.chatId; }

    /**
     * добавляет новую тему в {@link Theme#Themes}
     * @param themeName азвание темы
     * @param chatId id темы
     */
    static void AddThem(String themeName , String chatId){
        Theme.GetListThemes().add(new Theme(themeName , chatId));
    }//добавить тему

    /**
     * удаляет тему из {@link Theme#Themes}
     * @param themeName название
     * @param chatId id темы
     * @return true если тема удалена , false если такая тема не найдена в {@link Theme#Themes}
     */
    static boolean  DeleteTheme(String themeName , String chatId){
        for(Theme theme : Theme.GetListThemes() ){
            if(themeName.equals(theme.GetTheme()) && chatId.equals(theme.GetChatID())) {
                Theme.GetListThemes().remove(theme);
                return true;
            }
        }
        return false;
    }

    /**
     * получает название темы в скобках <> из текста
     * @param Text текс соооьщения
     * @return название темы
     */
    static String themName(String Text){
        String themName;
        try {
            themName = Text.substring(Text.indexOf('<') +1 , Text.indexOf('>'));
            if (themName.trim().equals(""))
                themName =  null;
        }
        catch (Exception e){
            themName = null;
        }
        return themName;

    }

    /**
     * проверяет наличие темы с таким названием в чате
     * @param themeName название темы
     * @param chatId id темы
     * @return true если тема с таким название есть в чате , false если нету
     */
    static boolean HasTheme(String themeName , String chatId){
        for(Theme theme : Theme.GetListThemes() ){
            if(themeName.equals(theme.GetTheme()) && chatId.equals(theme.GetChatID()))
                return true;
        }
        return false;
    }

    /**
     * позволяет добавлять сообщения по теме в список {@link Theme#massages}
     * @param message сообщение пользователя по определенной теме
     * @param themeName название темы
     * @param chatId id темы
     */
    static void AddMassegeToThem(Message message, String themeName , String chatId){
        for(Theme theme : Theme.GetListThemes()){
            if(theme.GetTheme().equals(themeName)&& theme.GetChatID().equals(chatId))
                theme.GetMassages().add(message);
        }
    } //пометить сообщение темой

    /**
     * возвращает сообщения по определенной теме
     * @param themeName название темы
     * @param chatId id чата темы
     * @return список сообщений
     */
    static ArrayList<Message> GetMessagesPerTheam(String themeName , String chatId){

        for(Theme theme : Theme.GetListThemes()){
            if(theme.GetTheme().equals(themeName) &&theme.GetChatID().equals(chatId))
                return theme.GetMassages();

        }
        return null;
    } //получить сообщения по теме

    /**
     * добавляет нового подписчика на тему в список {@link Theme#subscribers}
     * @param user новый подписчик
     * @param themeName название темы
     * @param chatId id чата темы
     * @return true если удалось длбовить , false если темы нет или человек уже подписан на нее
     */
    static boolean AddSubscriber(User user , String themeName , String chatId){
        for(Theme theme : Theme.GetListThemes()){
            if(theme.GetTheme().equals(themeName)&& theme.GetChatID().equals(chatId)&& !theme.GetSubscribers().contains(user)){
                theme.GetSubscribers().add(user);
                return true;
            }
        }
        return false;
    }//подписатся на тему

    /**
     * удаляет пользователя из списка {@link Theme#subscribers}
     * @param userId id пользователя
     * @param themeName название темы
     * @param chatId id темы
     * @return true если получилось отписаться , false если тако темы нет или пользователь на нее не подписан
     */
    static boolean DeleteSubscriber(String userId , String themeName , String chatId){
        for(Theme theme : Theme.GetListThemes()){
            for(User user : theme.GetSubscribers()) {
                if (theme.GetTheme().equals(themeName) && theme.GetChatID().equals(chatId) && user.getId().toString().equals(userId)) {
                    theme.GetSubscribers().remove(user);
                    return true;
                }
            }
        }
        return false;
    }//отписаться от темы
}
