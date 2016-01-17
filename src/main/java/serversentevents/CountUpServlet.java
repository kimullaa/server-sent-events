package serversentevents;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CountUpServlet", urlPatterns = { "/countUp" }, asyncSupported = true)
public class CountUpServlet extends HttpServlet {
	// countUp対象
	int count;
	List<AsyncContext> queue = new ArrayList<>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		final AsyncContext context = req.startAsync();
		// タイムアウトしないようにmax値入れる
		context.setTimeout(Long.MAX_VALUE);
		queue.add(context);
	}

	@Override
	public void init() throws ServletException {
		super.init();
		// 5sごとにインクリメントする
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				count++;
				broadcast();
			}
		};
		timer.schedule(task, 5000, 5000);
	}

	synchronized public void broadcast() {
		CopyOnWriteArrayList<AsyncContext> target = new CopyOnWriteArrayList<>(queue);

		// SSEのためにコネクションはcloseしない
		for (AsyncContext context : target) {
			HttpServletResponse resp = (HttpServletResponse) context.getResponse();
			resp.setContentType("text/event-stream");
			resp.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer = resp.getWriter();
				writer.write("data: {\"count\":\"" + count + "\"}\n\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
