package de.uol.swp.common.game;

import de.uol.swp.common.card.*;
import de.uol.swp.common.card.event_card.EventCardFactory;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.map.City;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.marker.AntidoteMarker;
import de.uol.swp.common.marker.InfectionMarker;
import de.uol.swp.common.marker.OutbreakMarker;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.plague.exception.NoPlagueCubesFoundException;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.util.Color;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
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

    public static final int DEFAULT_MAX_NUMBER_OF_HAND_CARDS = 7;
    public static final int DEFAULT_NUMBER_OF_PLAGUE_CUBES = 24;
    public static final int DEFAULT_NUMBER_OF_RESEARCH_LABORATORIES = 6;
    public static final int DEFAULT_NUMBER_OF_INFECTION_CARDS_DRAWN_PER_PHASE_OF_INITIAL_PLAGUE_CUBE_DISTRIBUTION = 3;
    public static final int DEFAULT_NUMBER_OF_PLAGUE_CUBES_ADDED_TO_EVERY_FIELD_IN_FIRST_PHASE_OF_INITIAL_PLAGUE_CUBE_DISTRIBUTION = 3;
    public static final int DEFAULT_MAX_NUMBER_OF_PLAGUE_CUBES_PER_FIELD = 3;
    public static final int DEFAULT_NUMBER_OF_ACTIONS_PER_TURN = 4;
    public static final int DEFAULT_NUMBER_OF_PLAYER_CARD_TO_DRAW_PER_TURN = 2;

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
    @Getter
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
    @Getter
    private InfectionMarker infectionMarker;
    @Getter
    private CardStack<PlayerCard> playerDrawStack;
    @Getter
    private CardStack<PlayerCard> playerDiscardStack;
    @Getter
    private CardStack<InfectionCard> infectionDrawStack;
    @Getter
    private CardStack<InfectionCard> infectionDiscardStack;
    private List<PlayerTurn> turns;
    @Getter
    private boolean isWon;
    @Getter
    @Setter
    private boolean isLost;

    /**
     * Constructs a Game instance with a basic configuration.
     *
     * @param lobby the lobby where the game is hosted
     * @param type the type of game map to be used
     * @param players the list of players participating in the game
     * @param plagues the list of plagues that will be present in the game
     */
    public Game (Lobby lobby, MapType type, List<Player> players, List<Plague> plagues, int numberOfEpidemicCards) {
        this(
            lobby,
            type,
            players,
            plagues,
            DEFAULT_MAX_NUMBER_OF_HAND_CARDS,
            DEFAULT_NUMBER_OF_PLAGUE_CUBES,
            DEFAULT_NUMBER_OF_RESEARCH_LABORATORIES,
            numberOfEpidemicCards,
            DEFAULT_NUMBER_OF_INFECTION_CARDS_DRAWN_PER_PHASE_OF_INITIAL_PLAGUE_CUBE_DISTRIBUTION,
            DEFAULT_NUMBER_OF_PLAGUE_CUBES_ADDED_TO_EVERY_FIELD_IN_FIRST_PHASE_OF_INITIAL_PLAGUE_CUBE_DISTRIBUTION,
            DEFAULT_MAX_NUMBER_OF_PLAGUE_CUBES_PER_FIELD,
            DEFAULT_NUMBER_OF_ACTIONS_PER_TURN,
            DEFAULT_NUMBER_OF_PLAYER_CARD_TO_DRAW_PER_TURN
        );
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
        this.turns = new ArrayList<>();
        this.isWon = false;
        this.isLost = false;
        this.indexOfCurrentPlayer = 0;

        List<Integer> infectionLevels = Arrays.asList(2, 2, 2, 3, 3, 4, 4);
        this.infectionMarker = new InfectionMarker(infectionLevels);

        List<Integer> outbreakLevels = Arrays.asList(0, 1, 2, 3, 4, 5, 8);
        this.outbreakMarker = new OutbreakMarker(outbreakLevels);

        this.antidoteMarkers = new ArrayList<>();
        for (Plague plague : plagues) {
            this.antidoteMarkers.add(new AntidoteMarker(plague));
        }

        this.map = new GameMap(this, type);

        this.plagueCubes = new HashMap<>();

        createPlayerStacks();
        createInfectionStacks();

        createPlagueCubes();
        distributeInitialPlagueCubes();

        assignPlayersToStartingField();
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
     * Assigns players to their starting positions on the game map.
     * This method sets up the initial locations for all players based on game rules.
     */
    private void assignPlayersToStartingField() {
        final Field startingField = this.map.getStartingField();
        for (final Player player : this.playersInTurnOrder) {
            player.setCurrentField(startingField);
        }
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
        Set<Plague> plagueSet = map.getType().getUniquePlagues();
        for (Plague plague : plagueSet) {
            List<PlagueCube> plagueCubeList = new ArrayList<>();
            for (int i = 0; i < numberOfPlagueCubesPerColor; i++) {
                plagueCubeList.add(new PlagueCube(plague));
            }
            plagueCubes.put(plague, plagueCubeList);

        }
    }

    /**
     * Shuffles and prepares the player decks.
     * This method organizes the player cards into draw and discard stacks.
     */
    private void createPlayerStacks () {
        createPlayerDrawStack();
        this.playerDiscardStack = new CardStack<>();
    }

    /**
     * Creates the draw stack for player cards.
     * This method initializes and shuffles the player card stack.
     */
    private void createPlayerDrawStack() {
        this.playerDrawStack = new CardStack<>();
        this.playerDrawStack.addAll(createCityCards());
        EventCardFactory eventCardFactory = new EventCardFactory();
        this.playerDrawStack.addAll(eventCardFactory.createEventCards());


        List<CardStack<Card>> subStacks = new ArrayList<>();
        List<Card> allCards = new ArrayList<>(this.playerDrawStack);
        int cardsPerStack = allCards.size() / this.numberOfEpidemicCards;

        for (int i = 0; i < this.numberOfEpidemicCards; i++) {
            CardStack<Card> subStack = new CardStack<>();
            int substackStartIndex  = i * cardsPerStack;
            int substackEndIndex = (i == this.numberOfEpidemicCards - 1) ? allCards.size() : (i + 1) * cardsPerStack;
            subStack.addAll(allCards.subList(substackStartIndex , substackEndIndex));
            subStacks.add(subStack);
        }

        List<EpidemicCard> epidemicCards = createEpidemicCards();
        shuffleEpidemicCardsIntoPlayerSubStacks(epidemicCards, subStacks);

        this.playerDrawStack.clear();
        for (CardStack<Card> subStack : subStacks) {
            List<PlayerCard> playerCards = subStack.stream()
                    .filter(card -> card instanceof PlayerCard)
                    .map(card -> (PlayerCard) card)
                    .toList();
            this.playerDrawStack.addAll(playerCards);
        }
    }

    /**
     * Creates the epidemic cards for the game.
     *
     * @return a list of EpidemicCard objects
     */
    private List<EpidemicCard> createEpidemicCards() {
        List<EpidemicCard> epidemicCards = new ArrayList<>();
        for (int i = 0; i < this.numberOfEpidemicCards; i++) {
            epidemicCards.add(new EpidemicCard());
        }
        return epidemicCards;
    }

    /**
     * Creates the city cards for the game.
     *
     * @return a list of CityCard objects
     */
    private List<CityCard> createCityCards () {
        CardStack<CityCard> cityCards = new CardStack<>();
        List<Field> fields = map.getFields();
        for (Field field: fields) {
            cityCards.push(new CityCard(field));
        }
        return cityCards;
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
    private void shuffleEpidemicCardsIntoPlayerSubStacks(List<EpidemicCard> epidemicCards, List<CardStack<Card>> playerCardSubStacks) {
        for (int i = 0; i < epidemicCards.size(); i++) {
            CardStack<Card> subStack = playerCardSubStacks.get(i);
            subStack.push(epidemicCards.get(i));
            subStack.shuffle();
        }
    }

    /**
     * Creates the infection stacks used in the game.
     * This method sets up the draw and discard stacks for infection cards.
     */
    private void createInfectionStacks () {
        createInfectionDrawStack();
        this.infectionDiscardStack = new CardStack<>();
    }

    /**
     * Creates the infection draw stack.
     * This method initializes and shuffles the infection cards.
     */
    private void createInfectionDrawStack () {
        this.infectionDrawStack = new CardStack<>();
        this.infectionDrawStack.addAll(createInfectionCards());
        this.infectionDrawStack.shuffle();
    }

    /**
     * Creates the infection cards for the game.
     *
     * @return a list of InfectionCard objects
     */
    private CardStack<InfectionCard> createInfectionCards () {
        CardStack<InfectionCard> infectionCards = new CardStack<>();
        List<Field> fields = map.getFields();
        for (Field field: fields) {
            infectionCards.push(new InfectionCard(new Color(66, 104, 55), field));
        }
        return infectionCards;
    }

    /**
     * Distributes initial plague cubes at the start of the game.
     * This method sets up the initial state of the game board by placing plague cubes.
     */
    private void distributeInitialPlagueCubes () {
        for (int i = numberOfPlagueCubesAddedToEveryFieldInFirstPhaseOfInitialPlagueCubesDistribution; i > 0; i--) {
            List<InfectionCard> drawnCards = new ArrayList<>();

            for (int j = 0; j < numberOfInfectionCardsDrawnPerPhaseOfInitialPlagueCubeDistribution; j++) {
                InfectionCard card = infectionDrawStack.pop();
                drawnCards.add(card);
                infectionDiscardStack.push(card);
            }
            for (InfectionCard drawnCard : drawnCards) {
                City city = drawnCard.getCity();
                Field field = map.getFieldOfCity(city);
                distributePlagueCubes(i, field);
            }
        }
    }

    /**
     * Distributes a specified number of plague cubes to a given field.
     *
     * @param numberOfPlagueCubes the number of plague cubes to be distributed
     * @param field the field to distribute plague cubes to
     */
    private void distributePlagueCubes (int numberOfPlagueCubes, Field field) {
        for (int i = 0; i < numberOfPlagueCubes; i++) {
            field.infect();
        }
    }

    /**
     * Determines the order in which players take their turns.
     * This method sets up the turn order based on game rules.
     */
    private void determinePlayerOrder () {

    }

    /**
     * Retrieves a plague cube for the specified plague.
     * This method is used to get a cube of a specific plague color for game actions.
     *
     * @param plague the plague, to retrieve a cube
     * @return the PlagueCube object representing a cube of the specified plague
     */
    public PlagueCube getPlagueCubeOfPlague (Plague plague) {
        List<PlagueCube> cubes = plagueCubes.get(plague);

        if (cubes == null) {
            throw new NoPlagueCubesFoundException(plague.getName());
        }

        if (cubes.isEmpty()) {
            isLost = true;
        }

        return cubes.remove(0);
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
     * Returns whether there are any research laboratories on this game.
     *
     * @return {@code true} if {@link #researchLaboratories} is not empty, {@code false} otherwise
     */
    public boolean hasResearchLaboratory() {
        return !researchLaboratories.isEmpty();
    }

    /**
     * Adds an antidote marker to the game.
     * This method is used to track the progress of finding a cure for a disease.
     *
     * @param marker the AntidoteMarker to be added to the game
     */
    public void addAntidoteMarker (AntidoteMarker marker) {
        this.antidoteMarkers.add(marker);
    }

    /**
     * <p>
     *     Returns whether an {@link AntidoteMarker} of the specified {@link Plague} already exists on this {@link Game}.
     * </p>
     *
     * @param plague The {@link Plague} to look for
     * @return {@code true} if there is an {@link AntidoteMarker} of the specified {@link Plague}, {@code false} otherwise
     */
    public boolean hasAntidoteMarkerForPlague(final Plague plague) {
        for (final AntidoteMarker antidoteMarker : antidoteMarkers) {
            if (antidoteMarker.getPlague().equals(plague)) {
                return true;
            }
        }
        return false;
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
        return this.numberOfInfectionCardsDrawnPerPhaseOfInitialPlagueCubeDistribution;
    }

    /**
     * Retrieves the current player turn.
     *
     * @return the PlayerTurn object representing the current turn
     */
    public PlayerTurn getCurrentTurn() {
        return this.turns.get(this.turns.size() - 1);
    }

    /**
     * Adds the given {@link PlayerTurn} to {@link #turns}.
     *
     * @param playerTurn The newly started {@link PlayerTurn}
     */
    public void addPlayerTurn(final PlayerTurn playerTurn) {
        this.turns.add(playerTurn);
    }

    /**
     * Returns the {@link List} of fields stored on the {@link #map}.
     *
     * @return {@link List} of fields
     * @see Field
     * @see GameMap#getFields()
     */
    public List<Field> getFields() {
        return map.getFields();
    }

    /**
     * Returns the last element of {@link #playersInTurnOrder}.
     *
     * @return the current {@link Player}
     */
    public Player getCurrentPlayer() {
        return this.playersInTurnOrder.get(this.indexOfCurrentPlayer);
    }

    /**
     * Increases {@link #indexOfCurrentPlayer} by {@code 1} and accounts for overflow by keeping it in range:
     * {@code 0} <= {@link #indexOfCurrentPlayer} < {@link #playersInTurnOrder}.size()
     */
    public void nextPlayer() {
        this.indexOfCurrentPlayer = (this.indexOfCurrentPlayer + 1) % this.playersInTurnOrder.size();
    }
}
