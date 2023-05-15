
2023-05-15
- Decided to not introduce auth-state, far too much scope creep.
- Move devopts into it's own dialog fragment

2023-05-10
New interview prompt from -REDACTED-
- Genericize app, remove reference to -REDACTED-, move stocks specific to /stocks
- Update readme
- Will reuse main recycler
- Will introduce auth-state. For simplicity, userId will be open-ended string that is created at login screen (and may be persisted in db records)
- New header: Status, UserId, Refresh, DevOpts
- New Dialog Fragment: New/Edit
- Reorder-list support. Table: UserId, ElementId, Sequence. 
- onItemMoved(): Re-order element list, send to service to create a new Sequence list.

2023-04-28
UPDATE. (Final submission to -REDACTED-)
- Will split repository into two layers: a "use-case"-like layer called StocksService
- Will add debug repository/service to control whether we call the alternative endpoints.
- Did not unittest view level.
- Did not implement auto-refresh on fragment re-reaction (only on viewmodel creation)

2023-04-26
Initial design plan. (Take-Home Prompt by -REDACTED-)

Overall Design/Arch considerations:
- Implement MVVM, with persistence in the repository. Rationale: Considered to be best for modern Android. (... and personally, the best for any app)

- Will implement with KMM, but only Android portion. Rationale: My most recent project is KMM, and my most recent "muscle memory" is with Koin and Ktor-client as opposed to Hilt/Dagger and OkHttp/retrofit.

- Note: I've not yet worked with SqlDelight, but my usage of RoomDB is well over a year ago which would have me reviewing documentation and examples anyway. I trust that it will be very easy to work with (*wink*), and it has the minimum requirements I need; the ability to observe a query with a Flow<T> that updates when data changes, and has an in-memory option for tests.

- Will use traditional Android views instead of compose. Rationale: I understand well the architecture and implementation of a UI with compose, including navigation. However I have very little experience in the actual UI layout aspect of it, can potentially add a lot of upfront cost in terms of time.

- Will implement JetPack Navigation. While there is no initial need for navigation, it will add very little in terms of upfront cost, but leaves the door open for additional features down the road. (Will be able to copy-pasta some code from a template)

Data:
- Will implement three data tables, "portfolio", "stock" and "portfolio-stocks". Relatively low upfront cost providing a lot of flexibility for future feature implementation.

- Initially provide all stock data in a single collection to a recycler view, however the separate tables will allow us, if necessary in the future for scale, to provide only a ticker list to the recycler view, and have each recycler cell fetch the individual stock data.

- Will timestamp every portfolio and stock on refresh, useful for two reasons: provides the ability to indicate stale data in the UI, and useful for cleaning up old items in the databases. (may or may not actually implement this, but again- low upfront cost)

Domain stack:
- ViewModel, Repository, Data Sources. No use-case layer. Rationale: in this initial implementation, use-case layers would effectively be no-ops. Part of the MVVM advantage of single direction dependencies, is the ability to add or remove layers with relative ease. The upfront cost of no-op use-case layers now provides little to no benefit in the future.

UX:
- Title cell, with a recycler view that lists the stock data. Will put to use as much of the data that is available without making the UI too noisy.

- Auto-refresh upon ViewModel creation. 

- Auto-refresh upon Fragment re-creation (only if a particular amount of time has passed)

- Provide a manual refresh of some sort.

- Visual indicator that refresh is in progress.

- Visual representation of stale data (greyscale or dim visuals).

- Errors shown in toasts.

Action Plan:
- Implement tests as we go, as needed.

- Commit KMM template.

- Copy-Pasta JetPack navigation template into project.

- Create domain skeleton. Empty objects with appropriate injections.

- Play with API a bit with Postman.

- Implement persistence module.

- Implement remote data module.

- Implement repository.

- Implement ViewModel, and skeleton views.

- Beef up error handling and reporting.

- Make the UI look nice.



