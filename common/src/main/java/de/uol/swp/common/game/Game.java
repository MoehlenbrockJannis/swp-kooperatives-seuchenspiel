package de.uol.swp.common.game;

import de.uol.swp.common.card.*;
import de.uol.swp.common.card.event_card.EventCard;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.map.*;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.marker.AntidoteMarker;
import de.uol.swp.common.marker.InfectionMarker;
import de.uol.swp.common.marker.OutbreakMarker;
import lombok.*;
import org.reflections.Reflections;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * The Game class represents the core of the pandemic game.
 * It handles the game setup, player actions, game state management,
 * and checks for win/lose conditions.
 * <p>
 * This class is responsible for managing players, diseases, cards,
 * and various game markers. It provides methods to initialize the game,
 * handle turns, and determine the game's outcome.
 */
public class Game implements Serializable {

    public static final int MIN_NUMBER_OF_PLAYERS = 2;
    public static final int MAX_NUMBER_OF_PLAYERS = 4;
    private static final String PATH_TO_EVENT_CARDS = "de.uol.swp.common.card.event_card";

    private static final Map<Integer, Integer> AMOUNT_OF_PLAYERS_AND_STARTING_HAND_CARDS = Map.of(
            4, 2,
            3, 3,
            2, 4
    );

    @Getter
    @Setter
    private int id;
    @Getter
    private Lobby lobby;
    @Getter
    private int maxHandCards;
    private int numberOfPlagueCubesPerColor;
    private int numberOfResearchLaboratories;
    private int numberOfEpidemicCards;
    private int numberOfInfectionCardsDrawnPerPhaseOfInitialPlagueCubeDistribution;
    private int numberOfPlagueCubesAddedToEveryFieldInFirstPhaseOfInitialPlagueCubesDistribution;
    @Getter
    private int maxNumberOfPlagueCubesPerField;
    @Getter
    private int numberOfActionsPerTurn;
    private int numberOfPlayerCardsToDrawPerTurn;
    @Getter
    private GameMap map;
    @Getter
    private int indexOfCurrentPlayer;
    @Getter
    private List<Player> playersInTurnOrder;
    @Getter
    private List<Plague> plagues;
    private Map<Plague, List<PlagueCube>> plagueCubes;
    private List<ResearchLaboratory> researchLaboratories;
    private List<AntidoteMarker> antidoteMarkers;
    private OutbreakMarker outbreakMarker;
    private InfectionMarker infectionMarker;
    @Getter
    private CardStack<PlayerCard> playerDrawStack;
    @Getter
    private CardStack<PlayerCard> playerDiscardStack;
    private CardStack<InfectionCard> infectionDrawStack;
    private CardStack<InfectionCard> infectionDiscardStack;
    private List<PlayerTurn> turns;
    @Getter
    private boolean isWon;
    @Getter
    private boolean isLost;

    /**
     * Constructs a Game instance with a basic configuration.
     *
     * @param lobby the lobby where the game is hosted
     * @param type the type of game map to be used
     * @param players the list of players participating in the game
     * @param plagues the list of plagues that will be present in the game
     */
    public Game (Lobby lobby, MapType type, List<Player> players, List<Plague> plagues) {
        this(
            lobby,
            type,
            players,
            plagues,
            7,
            24,
            6,
            6,
            1,
            3,
            3,
            4,
            2
        );
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Game game = (Game) obj;
        return this.id == game.id;
    }

    /**
     * Constructs a Game instance with detailed configuration.
     *
     * @param lobby the lobby where the game is hosted
     * @param type the type of game map to be used
     * @param players the list of players participating in the game
     * @param plagues the list of plagues that will be present in the game
     * @param maxHandCards the maximum number of cards a player can hold
     * @param numberOfPlagueCubesPerColor the number of plague cubes per color
     * @param numberOfResearchLaboratories the number of research laboratories in the game
     * @param numberOfEpidemicCards the number of epidemic cards in the player deck
     * @param numberOfInfectionCardsDrawnPerPhaseOfInitialPlagueCubeDistribution
     *        the number of infection cards drawn during initial setup
     * @param numberOfPlagueCubesAddedToEveryFieldInFirstPhaseOfInitialPlagueCubesDistribution
     *        the number of plague cubes added to each field in the first phase of setup
     * @param maxNumberOfPlagueCubesPerField the maximum number of plague cubes allowed per field
     * @param numberOfActionsPerTurn the number of actions a player can perform per turn
     * @param numberOfPlayerCardsToDrawPerTurn the number of player cards drawn per turn
     */
    public Game (
            Lobby lobby,
            MapType type,
            List<Player> players,
            List<Plague> plagues,
            int maxHandCards,
            int numberOfPlagueCubesPerColor,
            int numberOfResearchLaboratories,
            int numberOfEpidemicCards,
            int numberOfInfectionCardsDrawnPerPhaseOfInitialPlagueCubeDistribution,
            int numberOfPlagueCubesAddedToEveryFieldInFirstPhaseOfInitialPlagueCubesDistribution,
            int maxNumberOfPlagueCubesPerField,
            int numberOfActionsPerTurn,
            int numberOfPlayerCardsToDrawPerTurn
    ) {
        this.lobby = lobby;
        this.playersInTurnOrder = players;
        this.plagues = plagues;
        this.maxHandCards = maxHandCards;
        this.numberOfPlagueCubesPerColor = numberOfPlagueCubesPerColor;
        this.numberOfResearchLaboratories = numberOfResearchLaboratories;
        this.numberOfEpidemicCards = numberOfEpidemicCards;
        this.numberOfInfectionCardsDrawnPerPhaseOfInitialPlagueCubeDistribution =
            numberOfInfectionCardsDrawnPerPhaseOfInitialPlagueCubeDistribution;
        this.numberOfPlagueCubesAddedToEveryFieldInFirstPhaseOfInitialPlagueCubesDistribution =
            numberOfPlagueCubesAddedToEveryFieldInFirstPhaseOfInitialPlagueCubesDistribution;
        this.maxNumberOfPlagueCubesPerField = maxNumberOfPlagueCubesPerField;
        this.numberOfActionsPerTurn = numberOfActionsPerTurn;
        this.numberOfPlayerCardsToDrawPerTurn = numberOfPlayerCardsToDrawPerTurn;
        this.isWon = false;
        this.isLost = false;
        this.indexOfCurrentPlayer = 0;

        this.map = new GameMap(this, type);
        createPlayerStacks();
    }

    /**
     * Assigns players to their starting positions on the game map.
     * This method sets up the initial locations for all players based on game rules.
     */
    private void assignPlayersToStartingField () {

    }

    /**
     * Initializes research laboratories on the game map.
     * This method creates the research laboratories that players can use during the game.
     */
    private void createResearchLaboratories () {

    }

    /**
     * Adds a research laboratory to the starting field.
     * This method places a research laboratory on the initial field where players begin.
     */
    private void addResearchLaboratoryToStartingField () {

    }

    /**
     * Creates plague cubes for each plague type in the game.
     * This method generates the cubes that represent the spread of diseases.
     */
    private void createPlagueCubes () {

    }

    /**
     * Shuffles and prepares the player decks.
     * This method organizes the player cards into draw and discard stacks.
     */
    private void createPlayerStacks () {
        createPlayerDrawStack();
        Collections.shuffle(this.playerDrawStack,new Random());
        this.playerDiscardStack = new CardStack<PlayerCard>();
    }

    /**
     * Creates the draw stack for player cards.
     * This method initializes and shuffles the player card stack.
     */
    private void createPlayerDrawStack () {
        this.playerDrawStack = new CardStack<PlayerCard>();
        List<Field> fields = map.getFields();
        for (Field field: fields) {
            this.playerDrawStack.push(new CityCard(field));
        }
        List<EventCard> eventCards = createEventCards();
        for (EventCard eventCard: eventCards) {
            this.playerDrawStack.push(eventCard);
        }

    }

    /**
     * Creates the epidemic cards for the game.
     *
     * @return a list of EpidemicCard objects
     */
    private List<EpidemicCard> createEpidemicCards () {
        return new ArrayList<EpidemicCard>();
    }

    /**
     * Creates the city cards for the game.
     *
     * @return a list of CityCard objects
     */
    private List<CityCard> createCityCards () {
        return new ArrayList<CityCard>();
    }

    /**
     * Creates the event cards for the game.
     *
     * @return a list of EventCard objects
     */
    private List<EventCard> createEventCards () {
        List<EventCard> eventCards = new ArrayList<>();
        Reflections reflections = new Reflections(PATH_TO_EVENT_CARDS);
        for (Class<? extends EventCard> eventCard : reflections.getSubTypesOf(EventCard.class)) {
            if(!Modifier.isAbstract(eventCard.getModifiers())) {
                eventCards.add(createEventCard(eventCard));
            }
        }
        return eventCards;
    }

    private EventCard createEventCard (Class<? extends EventCard> eventCard) {
        EventCard eventCardInstance = null;
        try {
            eventCardInstance = eventCard.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return eventCardInstance;
    }

    /**
     * Assigns player cards to the players based on the initial setup.
     *
     * @param cards the stack of player cards to be distributed among players
     */
    private void assignPlayerCardsToPlayers (CardStack<PlayerCard> cards) {

    }

    /**
     * Shuffles epidemic cards into the player card stacks.
     * This method ensures that epidemic cards are distributed evenly across substacks.
     *
     * @param epidemicCards the list of epidemic cards to be shuffled into the stacks
     * @param playerCardSubStacks the list of player card substack
     */
    private void shuffleEpidemicCardsIntoPlayerSubStacks (
            List<EpidemicCard> epidemicCards,
            List<CardStack<Card>> playerCardSubStacks
            ) {

    }

    /**
     * Creates the infection stacks used in the game.
     * This method sets up the draw and discard stacks for infection cards.
     */
    private void createInfectionStacks () {

    }

    /**
     * Creates the infection draw stack.
     * This method initializes and shuffles the infection cards.
     */
    private void createInfectionDrawStack () {

    }

    /**
     * Distributes initial plague cubes at the start of the game.
     * This method sets up the initial state of the game board by placing plague cubes.
     */
    private void distributeInitialPlagueCubes () {

    }

    /**
     * Distributes a specified number of plague cubes to the game board.
     *
     * @param numberOfPlagueCubes the number of plague cubes to be distributed
     */
    private void distributePlagueCubes (int numberOfPlagueCubes) {

    }

    /**
     * Determines the order in which players take their turns.
     * This method sets up the turn order based on game rules.
     */
    private void determinePlayerOrder () {

    }

    /**
     * Starts a new turn for the current player.
     * This method handles the beginning of a new turn, returning the PlayerTurn object representing the current turn.
     *
     * @return the PlayerTurn object representing the current turn
     */
    private PlayerTurn startNewTurn () {
        // TODO: 05.09.2024
        return null;
    }

    /**
     * Retrieves a plague cube for the specified plague.
     * This method is used to get a cube of a specific plague color for game actions.
     *
     * @param plague the plague, to retrieve a cube
     * @return the PlagueCube object representing a cube of the specified plague
     */
    public PlagueCube getPlagueCubeOfPlague (Plague plague) {
        return new PlagueCube(plague);
    }

    /**
     * Adds a plague cube to the game map.
     * This method handles the addition of a plague cube to the appropriate location on the map.
     *
     * @param plagueCube the plague cube to be added to the map
     */
    public void addPlagueCube (PlagueCube plagueCube) {

    }

    /**
     * Creates and returns a new research laboratory.
     *
     * @return a new `ResearchLaboratory` instance
     */
    public ResearchLaboratory getResearchLaboratory () {
        return new ResearchLaboratory();
    }

    /**
     * Adds an antidote marker to the game.
     * This method is used to track the progress of finding a cure for a disease.
     *
     * @param marker the AntidoteMarker to be added to the game
     */
    public void addAntidoteMarker (AntidoteMarker marker) {

    }

    /**
     * Triggers an outbreak in the game.
     * This method handles the logic for when a disease spreads beyond control.
     */
    public void startOutbreak () {

    }

    /**
     * Gets the number of infection cards to draw per turn.
     *
     * @return the number of infection cards to draw per turn
     */
    public int getNumberOfInfectionCardsToDrawPerTurn () {
        return 0;
    }

    /**
     * Retrieves the current player turn.
     *
     * @return the PlayerTurn object representing the current turn
     */
    public PlayerTurn getCurrentTurn () {
        // TODO: 05.09.2024  
        return null;
    }
}
