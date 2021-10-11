package taa.command;

import taa.Ui;
import taa.assessment.Assessment;
import taa.assessment.AssessmentList;
import taa.exception.TaaException;
import taa.module.Module;
import taa.module.ModuleList;
import taa.student.Student;
import taa.student.StudentList;

/**
 * Class that deals with the command for the listing of marks.
 */
public class ListMarksCommand extends Command {
    private static final String KEY_MODULE_CODE = "c";
    private static final String KEY_ASSESSMENT_NAME = "a";
    private static final String[] ADD_ASSESSMENT_ARGUMENT_KEYS = {
        KEY_MODULE_CODE,
        KEY_ASSESSMENT_NAME
    };

    private static final String MESSAGE_LIST_MARKS_HEADER = "Here is the list of students and "
            + "their marks for %s:";
    private static final String MARKS_LIST_EMPTY = "There are no marks inputted!";

    private static final String MESSAGE_FORMAT_LIST_MARKS_USAGE = "Usage: %s "
            + "%s/<MODULE_CODE> %s/<ASSESSMENT_NAME>";

    public ListMarksCommand(String argument) {
        super(argument, ADD_ASSESSMENT_ARGUMENT_KEYS);
    }

    /**
     * Returns a string with the student name and the marks associated with the student.
     *
     * @param studentName Name of the student.
     * @param marks Marks of the student.
     * @return String with the name and marks of the student separated with a vertical line.
     */
    public String marksToString(String studentName, double marks) {
        return studentName + " | " + marks;
    }

    /**
     * Checks for errors before calling the function that
     * lists the student and the marks they attained for an assessment.
     *
     * @param moduleList List of modules.
     * @param ui The ui instance to handle interactions with the user.
     * @throws TaaException When list marks command is invalid.
     */
    @Override
    public void execute(ModuleList moduleList, Ui ui) throws TaaException {
        if (argument.isEmpty()) {
            throw new TaaException(getUsageMessage());
        }
        if (!checkArgumentMap()) {
            throw new TaaException(getMissingArgumentMessage());
        }

        String moduleCode = argumentMap.get(KEY_MODULE_CODE);
        Module module = moduleList.getModule(moduleCode);
        if (module == null) {
            throw new TaaException(MESSAGE_MODULE_NOT_FOUND);
        }

        StudentList studentList = module.getStudentList();
        if (studentList.getSize() <= 0) {
            throw new TaaException(MESSAGE_NO_STUDENTS);
        }

        AssessmentList assessmentList = module.getAssessmentList();
        Assessment assessment = assessmentList.getAssessment(argumentMap.get(KEY_ASSESSMENT_NAME));
        if (assessment == null) {
            throw new TaaException(MESSAGE_INVALID_ASSESSMENT_NAME);
        }

        listMarks(ui, module, assessment);
    }

    /**
     * Lists the student and the marks they attained for an assessment.
     *
     * @param ui The ui instance to handle interactions with the user.
     * @param module The module that the student and assessment belong to.
     */
    private void listMarks(Ui ui, Module module, Assessment assessment) {
        StudentList studentList = module.getStudentList();
        String assessmentName = assessment.getName();

        StringBuilder stringBuilder = new StringBuilder(String.format(MESSAGE_LIST_MARKS_HEADER, assessmentName));
        int studentIndex = 0;
        for (Student student : studentList.getStudents()) {
            studentIndex += 1;
            if (student.marksExist(assessmentName)) {
                stringBuilder.append("\n");
                stringBuilder.append(studentIndex);
                stringBuilder.append(". ");
                stringBuilder.append(marksToString(student.getName(), student.getMarks(assessmentName)));
            } else {
                stringBuilder.append("\n");
                stringBuilder.append(studentIndex);
                stringBuilder.append(". ");
                stringBuilder.append(student.getName());
                stringBuilder.append(" | ");
                stringBuilder.append("NIL");
            }
        }
        if (studentIndex <= 0) {
            ui.printMessage(MARKS_LIST_EMPTY);
        } else {
            ui.printMessage(stringBuilder.toString());
        }
    }

    /**
     * Returns the usage message of the list marks command.
     *
     * @return String which contains the usage message.
     */
    @Override
    protected String getUsageMessage() {
        return String.format(
                MESSAGE_FORMAT_LIST_MARKS_USAGE,
                COMMAND_LIST_MARKS,
                KEY_MODULE_CODE,
                KEY_ASSESSMENT_NAME
        );
    }
}