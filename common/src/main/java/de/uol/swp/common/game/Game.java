package de.uol.swp.common.game;

import de.uol.swp.common.card.*;
import de.uol.swp.common.card.event_card.EventCard;
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
import de.uol.swp.common.triggerable.Triggerable;
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
    public static final int EPIDEMIC_CARD_DRAW_NUMBER_OF_INFECTIONS = 3;
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
    private GameDifficulty difficulty;
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
    @Getter
    private Map<Plague, List<PlagueCube>> plagueCubes;
    @Getter
    private List<ResearchLaboratory> researchLaboratories;
    private List<AntidoteMarker> antidoteMarkers;
    private OutbreakMarker outbreakMarker;
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
    @Getter
    @Setter
    private boolean requiresTextMessageMovingResearchLaboratory;
    @Getter
    @Setter
    private boolean researchLaboratoryButtonClicked = false;

    /**
     * Constructs a Game instance with a basic configuration.
     *
     * @param lobby the lobby where the game is hosted
     * @param type the type of game map to be used
     * @param players the list of players participating in the game
     * @param plagues the list of plagues that will be present in the game
     * @param difficulty the number of epidemic cards in the player deck
     */
    public Game (Lobby lobby, MapType type, List<Player> players, List<Plague> plagues, GameDifficulty difficulty) {
        this(
            lobby,
            type,
            players,
            plagues,
            DEFAULT_MAX_NUMBER_OF_HAND_CARDS,
            DEFAULT_NUMBER_OF_PLAGUE_CUBES,
            DEFAULT_NUMBER_OF_RESEARCH_LABORATORIES,
            difficulty,
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
     * @param difficulty the number of epidemic cards in the player deck
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
            GameDifficulty difficulty,
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
        this.difficulty = difficulty;
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

        this.map = new GameMap(this, type);

        this.plagueCubes = new HashMap<>();
        this.researchLaboratories = new ArrayList<>();
        this.requiresTextMessageMovingResearchLaboratory = true;
        this.researchLaboratoryButtonClicked = false;
        this.antidoteMarkers = new ArrayList<>();

        createPlayerStacks();

        createInfectionStacks();

        createPlagueCubes();
        distributeInitialPlagueCubes();

        assignPlayersToStartingField();
        initializeStartResearchLaboratory();
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
     * Adds a research lab to the start of the game in Atlanta
     */
    public void initializeStartResearchLaboratory() {
        ResearchLaboratory startResearchLaboratory = new ResearchLaboratory();
        Field field = this.map.getStartingField();
        researchLaboratories.add(startResearchLaboratory);
        field.buildResearchLaboratory(startResearchLaboratory);
    }

    /**
     * Shuffles and prepares the player decks.
     * This method organizes the player cards into draw and discard stacks.
     */
    private void createPlayerStacks() {
        initializeBasePlayerStack();
        assignPlayerCardsToPlayers(this.playerDrawStack);
        divideAndAddEpidemicCards(this.playerDrawStack);
    }

    /**
     * Divides the player stack into substacks, adds epidemic cards, and recombines them.
     * This method handles the epidemic card integration process.
     *
     * @param playerStack The stack to divide and modify
     */
    private void divideAndAddEpidemicCards(CardStack<PlayerCard> playerStack) {
        int numberOfEpidemicCards = difficulty.getNumberOfEpidemicCards();
        int cardsPerStack = playerStack.size() / numberOfEpidemicCards;
        int remainingCards = playerStack.size() % numberOfEpidemicCards;

        List<CardStack<PlayerCard>> subStacks = new ArrayList<>();

        for (int i = 0; i < numberOfEpidemicCards; i++) {
            CardStack<PlayerCard> subStack = createSubStackWithEpidemicCard(playerStack, cardsPerStack, i < remainingCards);
            subStacks.add(subStack);
        }

        playerStack.clear();
        for (CardStack<PlayerCard> subStack : subStacks) {
            playerStack.addAll(subStack);
        }
    }

    /**
     * Creates a substack with the specified number of cards and adds an epidemic card.
     *
     * @param sourceStack The source stack to take cards from
     * @param baseSize The base number of cards for this stack
     * @param addExtraCard Whether to add an extra card for remainder distribution
     * @return The created and shuffled substack
     */
    private CardStack<PlayerCard> createSubStackWithEpidemicCard(CardStack<PlayerCard> sourceStack, int baseSize, boolean addExtraCard) {
        CardStack<PlayerCard> subStack = new CardStack<>();
        int currentStackSize = baseSize + (addExtraCard ? 1 : 0);

        for (int j = 0; j < currentStackSize && !sourceStack.isEmpty(); j++) {
            subStack.push(sourceStack.pop());
        }
        subStack.push(new EpidemicCard());
        subStack.shuffle();

        return subStack;
    }


    /**
     * Initializes the player draw stack with city and event cards.
     * This creates the base deck before epidemic cards are added.
     */
    private void initializeBasePlayerStack() {
        this.playerDiscardStack = new CardStack<>();
        this.playerDrawStack = new CardStack<>();
        this.playerDrawStack.addAll(createCityCards());

        EventCardFactory eventCardFactory = new EventCardFactory();
        this.playerDrawStack.addAll(eventCardFactory.createEventCards());

        this.playerDrawStack.shuffle();
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
     * @param cardStack the stack of player cards to be distributed among players
     */
    private void assignPlayerCardsToPlayers(CardStack<PlayerCard> cardStack) {
        int numberOfPlayers = playersInTurnOrder.size();
        int cardsPerPlayer = AMOUNT_OF_PLAYERS_AND_STARTING_HAND_CARDS.get(numberOfPlayers);

        for (Player player : playersInTurnOrder) {
            for (int i = 0; i < cardsPerPlayer && !cardStack.isEmpty(); i++) {
                player.addHandCard(cardStack.pop());
            }
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
            field.infectField();
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
     * Gets the number of infection cards to draw per turn based on the current infection level.
     * The number of cards drawn increases as the infection level rises:
     * @return the number of infection cards to draw per turn based on current infection level
     */
    public int getNumberOfInfectionCardsToDrawPerTurn () {
        return this.infectionMarker.getLevelValue();
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

    /**
     * Returns a {@link List} of all triggerables currently present in this {@link Game}.
     *
     * @return a {@link List} of all triggerables
     */
    public List<Triggerable> getTriggerables() {
        return getPlayersInTurnOrder().stream()
                .flatMap(player -> getAndPreparePlayerEventCards(player).stream())
                .map(Triggerable.class::cast)
                .toList();
    }

    /**
     * Extracts all {@link EventCard} hand cards from given {@link Player} and prepares them for use.
     *
     * @param player {@link Player} to extract all {@link EventCard} hand cards from
     * @return {@link List} of {@link EventCard}
     * @see EventCard#setGame(Game)
     * @see EventCard#setPlayer(Player)
     */
    private List<EventCard> getAndPreparePlayerEventCards(final Player player) {
        return player.getHandCards().stream()
                .filter(EventCard.class::isInstance)
                .map(EventCard.class::cast)
                .map(eventCard -> {
                    eventCard.setPlayer(player);
                    eventCard.setGame(this);
                    return eventCard;
                })
                .toList();
    }

    /**
     * Returns an {@link Optional} of a {@link Player} in {@link #playersInTurnOrder} that is equal to given {@link Player}.
     *
     * @return {@link Optional} of a {@link Player} in {@link #playersInTurnOrder} that is equal to given {@link Player}
     * @see Player#equals(Object)
     */
    public Optional<Player> findPlayer(final Player player) {
        return playersInTurnOrder.stream()
                .filter(p -> p.equals(player))
                .findFirst();
    }

    /**
     * Returns an {@link Optional} of a {@link Field} in {@link #map} that is equal to given {@link Field}.
     *
     * @return {@link Optional} of a {@link Field} in {@link #map} that is equal to given {@link Field}
     * @see Field#equals(Object)
     */
    public Optional<Field> findField(final Field field) {
        return map.getFields().stream()
                .filter(f -> f.equals(field))
                .findFirst();
    }

    /**
     * Increases the infection level marker
     */
    public void increaseInfectionLevel() {
        this.infectionMarker.increaseLevel();
    }
}
