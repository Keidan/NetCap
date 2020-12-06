package fr.ralala.netcap.ui.chooser;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import fr.ralala.netcap.R;
import fr.ralala.netcap.ui.utils.UI;

/**
 *******************************************************************************
 * <p><b>Project NetCap</b><br/>
 * File chooser activity
 * </p>
 * @author Keidan
 *
 *******************************************************************************
 */
public abstract class AbstractFileChooserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
  public static final String FILECHOOSER_TYPE_KEY = "type";
  public static final String FILECHOOSER_TITLE_KEY = "title";
  public static final String FILECHOOSER_MESSAGE_KEY = "message";
  public static final String FILECHOOSER_SHOW_KEY = "show";
  public static final String FILECHOOSER_FILE_FILTER_KEY = "file_filter";
  public static final String FILECHOOSER_DEFAULT_DIR = "default_dir";
  public static final String FILECHOOSER_USER_MESSAGE = "user_message";
  public static final int FILECHOOSER_TYPE_FILE_ONLY = 0;
  public static final int FILECHOOSER_TYPE_DIRECTORY_ONLY  = 1;
  public static final int FILECHOOSER_TYPE_FILE_AND_DIRECTORY = 2;
  public static final int FILECHOOSER_SHOW_DIRECTORY_ONLY = 1;
  public static final int FILECHOOSER_SHOW_FILE_AND_DIRECTORY = 2;
  public static final String FILECHOOSER_FILE_FILTER_ALL = "*";
  protected File mCurrentDir = null;
  protected File mDefaultDir = null;
  private FileChooserArrayAdapter mAdapter = null;
  private String mConfirmMessage = null;
  private String mConfirmTitle = null;
  private String mUserMessage = null;
  private String mFileFilter = FILECHOOSER_FILE_FILTER_ALL;
  private int mType = FILECHOOSER_TYPE_FILE_AND_DIRECTORY;
  private int mShow = FILECHOOSER_SHOW_FILE_AND_DIRECTORY;
  private ListView mListview = null;


  /**
   * Confirm dialog.
   * @param o The selected option.
   */
  private void confirm(final FileChooserItem o) {
    UI.showConfirmDialog(
        this,
        mConfirmTitle, mConfirmMessage + "\n" + o.getPath(),
        (view) -> onFileSelected(o));
  }

  /**
   * Called when the activity is created.
   * @param savedInstanceState The saved instance state.
   */
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filechooser);
    mListview = findViewById(R.id.list);
    Bundle b = getIntent().getExtras();
    if (b != null && b.containsKey(FILECHOOSER_TYPE_KEY))
      mType = Integer.parseInt(b.getString(FILECHOOSER_TYPE_KEY));
    if (b != null && b.containsKey(FILECHOOSER_TITLE_KEY))
      mConfirmTitle = b.getString(FILECHOOSER_TITLE_KEY);
    if (b != null && b.containsKey(FILECHOOSER_MESSAGE_KEY))
      mConfirmMessage = b.getString(FILECHOOSER_MESSAGE_KEY);
    if (b != null && b.containsKey(FILECHOOSER_SHOW_KEY))
      mShow = Integer.parseInt(b.getString(FILECHOOSER_SHOW_KEY));
    if (b != null && b.containsKey(FILECHOOSER_FILE_FILTER_KEY))
      mFileFilter = b.getString(FILECHOOSER_FILE_FILTER_KEY);
    if (b != null && b.containsKey(FILECHOOSER_DEFAULT_DIR))
      mDefaultDir = new File(""+b.getString(FILECHOOSER_DEFAULT_DIR));
    if (b != null && b.containsKey(FILECHOOSER_USER_MESSAGE))
      mUserMessage = b.getString(FILECHOOSER_USER_MESSAGE);
    if(mConfirmTitle == null) mConfirmTitle = "title";
    if(mConfirmMessage == null) mConfirmMessage = "message";
    mCurrentDir = mDefaultDir;
    mListview.setLongClickable(true);
    mListview.setOnItemLongClickListener(this);
    mListview.setOnItemClickListener(this);
    fill(mCurrentDir);
    UI.toastLong(this, R.string.chooser_long_press_message);
  }

  /**
   * Fill the list.
   * @param f The new root folder.
   */
  protected void fill(final File f) {
    final File[] dirs = f.listFiles();
    this.setTitle(f.getAbsolutePath());
    final List<FileChooserItem> dir = new ArrayList<>();
    final List<FileChooserItem> fls = new ArrayList<>();
    if(dirs != null) {
      try {
        for (final File ff : dirs) {
          if (ff.isDirectory())
            dir.add(new FileChooserItem(ff.getName(), getString(R.string.chooser_folder), ff.getAbsolutePath(), ContextCompat.getDrawable(this, R.mipmap.ic_folder)));
          else if (mShow != FILECHOOSER_SHOW_DIRECTORY_ONLY) {
            if (isFiltered(ff))
              fls.add(new FileChooserItem(ff.getName(), getString(R.string.chooser_file_size) + ": " + ff.length(), ff
                  .getAbsolutePath(), ContextCompat.getDrawable(this, R.mipmap.ic_file)));
          }
        }
      } catch (final Exception e) {
        Log.e(getClass().getSimpleName(), "Exception : " + e.getMessage(), e);
      }
      Collections.sort(dir);
      if (!fls.isEmpty()) {
        Collections.sort(fls);
        dir.addAll(fls);
      }
    }
    dir.add(
        0,
        new FileChooserItem("..", getString(R.string.chooser_parent_directory), f.getParent(), ContextCompat.getDrawable(this, R.mipmap.ic_folder)));
    mAdapter = new FileChooserArrayAdapter(this, R.layout.file_view, dir);
    mListview.setAdapter(mAdapter);
  }

  /**
   * Tests if the input file is in the filter list.
   * @param file The file to test.
   * @return boolean
   */
  public boolean isFiltered(final File file) {
    StringTokenizer token = new StringTokenizer(mFileFilter, ",");
    while(token.hasMoreTokens()) {
      String filter = token.nextToken();
      if(filter.equals("*")) return true;
      if(file.getName().endsWith("." + filter)) return true;
    }
    return false;
  }

  /**
   * Called when an item is clicked.
   * @param l The AdapterView.
   * @param v The clicked view.
   * @param position The position in the AdapterView.
   * @param id Not used.
   */
  @Override
  public void onItemClick(final AdapterView<?> l, final View v,
                          final int position, final long id) {
    final FileChooserItem o = mAdapter.getItem(position);
    if (o == null || o.getPath() == null)
      return;
    if (o.getData().equalsIgnoreCase("folder")
        || o.getData().equalsIgnoreCase("parent directory")) {
      mCurrentDir = new File(o.getPath());
      fill(mCurrentDir);
    } else if (mType == FILECHOOSER_TYPE_FILE_ONLY)
      confirm(o);
  }

  public final boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
    final FileChooserItem o = mAdapter.getItem(position);
    if (o == null || o.getPath() == null)
      return false;
    boolean folder = false;
    if (o.getData().equalsIgnoreCase("folder"))
      folder = true;
    if (!o.getData().equalsIgnoreCase("parent directory")) {
      if (folder && mType == FILECHOOSER_TYPE_FILE_ONLY)
        return false;
      if (!folder && mType == FILECHOOSER_TYPE_DIRECTORY_ONLY)
        return false;
      confirm(o);
    }
    return true;
  }

  /**
   * Called when a file is selected.
   * @param opt The selected option.
   */
  protected void onFileSelected(final FileChooserItem opt) {
  }

  /**
   * Returns the user message.
   * @return String
   */
  public String getUserMessage() {
    return mUserMessage;
  }
}
