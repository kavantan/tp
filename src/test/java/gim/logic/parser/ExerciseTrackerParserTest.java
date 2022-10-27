package gim.logic.parser;

import static gim.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static gim.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static gim.logic.commands.CommandTestUtil.VALID_LEVEL_EASY;
import static gim.logic.parser.CliSyntax.PREFIX_LEVEL;
import static gim.testutil.Assert.assertThrows;
import static gim.testutil.TypicalIndexes.INDEX_FIRST_EXERCISE;
import static gim.testutil.TypicalIndexes.INDEX_LIST_FIRST_SECOND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import gim.logic.commands.AddCommand;
import gim.logic.commands.ClearCommand;
import gim.logic.commands.DeleteCommand;
import gim.logic.commands.EditCommand;
import gim.logic.commands.EditCommand.EditExerciseDescriptor;
import gim.logic.commands.ExitCommand;
import gim.logic.commands.FilterCommand;
import gim.logic.commands.GenerateCommand;
import gim.logic.commands.HelpCommand;
import gim.logic.commands.ListCommand;
import gim.logic.parser.exceptions.ParseException;
import gim.model.exercise.Exercise;
import gim.model.exercise.NameContainsKeywordsPredicate;
import gim.testutil.EditExerciseDescriptorBuilder;
import gim.testutil.ExerciseBuilder;
import gim.testutil.ExerciseUtil;

public class ExerciseTrackerParserTest {

    private final ExerciseTrackerParser parser = new ExerciseTrackerParser();

    @Test
    public void parseCommand_add() throws Exception {
        Exercise exercise = new ExerciseBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(ExerciseUtil.getAddCommand(exercise));
        assertEquals(new AddCommand(exercise), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_EXERCISE.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_EXERCISE), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Exercise exercise = new ExerciseBuilder().build();
        EditExerciseDescriptor descriptor = new EditExerciseDescriptorBuilder(exercise).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_EXERCISE.getOneBased() + " " + ExerciseUtil.getEditExerciseDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_EXERCISE, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_filter() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FilterCommand command = (FilterCommand) parser.parseCommand(
                FilterCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FilterCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_generate() throws Exception {
        GenerateCommand command = (GenerateCommand) parser.parseCommand(GenerateCommand.COMMAND_WORD + " "
                + "1, 2 " + PREFIX_LEVEL + VALID_LEVEL_EASY);
        assertEquals(new GenerateCommand(INDEX_LIST_FIRST_SECOND, VALID_LEVEL_EASY), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
