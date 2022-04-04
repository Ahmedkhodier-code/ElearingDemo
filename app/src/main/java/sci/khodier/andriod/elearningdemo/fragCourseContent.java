package sci.khodier.andriod.elearningdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class fragCourseContent extends Fragment {

    //    private static final int RESULT_OK = 0;
    View view;
    TextView textview1;
    ImageView imageview1;
    PdfRenderer renderer;
    int total_pages = 0;
    int display_page = 0;
    public static final int PICK_FILE = 99;
    Button pickFile, prev, next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.frag_course_content, container, false);
        pickFile = view.findViewById(R.id.pick);
        prev = view.findViewById(R.id.prev);
        next = view.findViewById(R.id.next);
        textview1 = view.findViewById(R.id.textView1);
        imageview1 = view.findViewById(R.id.imageView);

        pickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                startActivityForResult(intent, PICK_FILE);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to previous page
                if (display_page > 0) {
                    display_page--;
                    _display(display_page);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to next page
                if (display_page < (total_pages - 1)) {
                    display_page++;
                    _display(display_page);
                }
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        if (requestCode == PICK_FILE) {
            System.out.println("1111111111111111111111111111111111111111111111111111111111111111111111");
            if (data != null) {
                Uri uri = data.getData();
                try {
                    ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(uri, "r");
                    renderer = new PdfRenderer(parcelFileDescriptor);
                    total_pages = renderer.getPageCount();
                    display_page = 0;
                    _display(display_page);
                } catch (FileNotFoundException fnfe) {

                } catch (IOException e) {

                }
            }
        }
    }

    private void _display(int _n) {
        if (renderer != null) {
            PdfRenderer.Page page = renderer.openPage(_n);
            Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            imageview1.setImageBitmap(mBitmap);
            page.close();
            textview1.setText((_n + 1) + "/" + total_pages);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (renderer != null) {
            renderer.close();
        }
    }

}